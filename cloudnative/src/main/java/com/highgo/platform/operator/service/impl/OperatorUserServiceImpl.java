package com.highgo.platform.operator.service.impl;

import com.highgo.platform.apiserver.model.vo.request.DatabaseVO;
import com.highgo.platform.apiserver.model.vo.request.DatabaseUserVO;
import com.highgo.platform.configuration.K8sClientConfiguration;
import com.highgo.platform.operator.cr.DatabaseCluster;
import com.highgo.platform.operator.cr.bean.user.User;
import com.highgo.platform.operator.service.OperatorUserService;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OperatorUserServiceImpl implements OperatorUserService {
    private static final Logger logger = LoggerFactory.getLogger(OperatorUserServiceImpl.class);

    @Autowired
    private K8sClientConfiguration k8sClientConfiguration;

    @Override
    public void resetPassword(String clusterId, String namespace, String crName, String userName, String password) {
        KubernetesClient kubernetesClient = k8sClientConfiguration.getAdminKubernetesClientById(clusterId);
        String userSecretName = crName+"-pguser-"+userName;
        Secret userSecret = kubernetesClient.secrets().inNamespace(namespace).withName(userSecretName).get();
        if(userSecret == null){
            logger.error("[OperatorUserServiceImpl.resetPassword] user secret is not exist. clusterId {}, namespace {}, crName {}, username {}", clusterId, namespace, crName, userName);
        }
        kubernetesClient.secrets().inNamespace(namespace).withName(userSecretName).patch(String.format("{\"stringData\":{\"password\":\"%s\",\"verifier\":\"\"}}", password));
        logger.info("[OperatorUserServiceImpl.resetPassword] reset password success. clusterId {}, namespace {}, crName {}, username {}", clusterId, namespace, crName, userName);

    }

    @Override
    public void createUser(String clusterId, String namespace, String inName, DatabaseUserVO databaseUserVO) {

        KubernetesClient kubernetesClient = k8sClientConfiguration.getAdminKubernetesClientById(clusterId);
        io.fabric8.kubernetes.client.dsl.Resource<DatabaseCluster> clusterResource = kubernetesClient.customResources(DatabaseCluster.class).inNamespace(namespace).withName(inName);
        DatabaseCluster databaseCluster = clusterResource.get();
        List<String> userNames = databaseCluster
                .getSpec()
                .getUsers()
                .stream()
                .map(User::getName)
                .collect(Collectors.toList());

        if(userNames.contains(databaseUserVO.getName())){
            throw new RuntimeException("The name " + databaseUserVO.getName() + " already exists!");
        }

        List<User> users = databaseCluster.getSpec().getUsers();
        User newUser = User.builder().name(databaseUserVO.getName()).options(databaseUserVO.getOption().name()).build();
        users.add(newUser);
        clusterResource.patch(databaseCluster);

    }

    @Override
    public void deleteDbUser(String clusterId, String namespace, String inName, String userName) {
        KubernetesClient kubernetesClient = k8sClientConfiguration.getAdminKubernetesClientById(clusterId);
        io.fabric8.kubernetes.client.dsl.Resource<DatabaseCluster> clusterResource = kubernetesClient.customResources(DatabaseCluster.class).inNamespace(namespace).withName(inName);
        DatabaseCluster databaseCluster = clusterResource.get();

        databaseCluster
                .getSpec()
                .getUsers()
                .removeIf(u -> u.getName().equals(userName));

        clusterResource.patch(databaseCluster);
    }

    @Override
    public void createDatabase(String clusterId, String namespace, String inName, DatabaseVO databaseVO) {
        KubernetesClient kubernetesClient = k8sClientConfiguration.getAdminKubernetesClientById(clusterId);
        io.fabric8.kubernetes.client.dsl.Resource<DatabaseCluster> clusterResource = kubernetesClient.customResources(DatabaseCluster.class).inNamespace(namespace).withName(inName);
        DatabaseCluster databaseCluster = clusterResource.get();
        if(StringUtils.isEmpty(databaseVO.getOwner())){
            databaseVO.setOwner("sysdba");
        }

        User user = databaseCluster
                .getSpec()
                .getUsers()
                .stream()
                .filter(u -> u.getName().equals(databaseVO.getOwner()))
                .findFirst()
                .get();

        if(user == null ){
            throw new RuntimeException("This user " + databaseVO.getOwner() + " does not exist in cr");
        }

        List<String> databases = user.getDatabases();
        if(databases == null ){
            databases = new ArrayList<>();
        }

        if (databases.contains(databaseVO.getDbName())) {
            logger.info("The user " + databaseVO.getDbName() + " already exists");
            return;
        }

        databases.add(databaseVO.getDbName());
        user.setDatabases(databases);

        clusterResource.patch(databaseCluster);
    }
}

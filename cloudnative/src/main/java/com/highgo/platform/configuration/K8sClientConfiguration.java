package com.highgo.platform.configuration;


import com.highgo.platform.apiserver.model.po.K8sClusterInfoPO;
import com.highgo.platform.apiserver.repository.K8sClusterInfoRepository;
import com.highgo.platform.errorcode.ClusterError;
import com.highgo.platform.errorcode.CommonError;
import com.highgo.platform.exception.ClusterException;
import com.highgo.platform.exception.CommonException;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.internal.KubeConfigUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.Optional;

/**
 * 获取k8sclient工具
 */
@Component
public class K8sClientConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(K8sClientConfiguration.class);

    @Autowired
    private K8sClusterInfoRepository k8sClusterInfoRepository;


    public KubernetesClient getAdminKubernetesClientById(String clusterId) {
        Optional<K8sClusterInfoPO> clusterConfigOptional = k8sClusterInfoRepository.findByClusterId(clusterId);
        if(!clusterConfigOptional.isPresent()){
            logger.error("[K8sClientConfiguration.getAdminKubernetesClientById] clusterConfig is not exits. clusterId is {}", clusterId);
            throw new CommonException(CommonError.COMMON_ERROR);
        }
        K8sClusterInfoPO k8sClusterInfoPO = clusterConfigOptional.get();
        try {
            InetAddress geek = InetAddress.getByName(k8sClusterInfoPO.getServerUrl());
            if(!geek.isReachable(5000)){
                logger.error("[K8sClientConfiguration.getAdminKubernetesClientById] The IP address of the master node is not connected, k8s id is {}", clusterId);
                throw new ClusterException(ClusterError.CLUSTER_MASTER_IP_UNREACHABLE);
            }
            //校验config
            KubeConfigUtils.parseConfigFromString(k8sClusterInfoPO.getConfig());
        } catch (Exception e) {
            logger.error("[K8sClientConfiguration.getAdminKubernetesClientById] The client failed to connect to k8s, k8s id is {}", clusterId);
            throw new ClusterException(ClusterError.CLUSTER_CONFIG_INVALID);
        }
        Config config = Config.fromKubeconfig(k8sClusterInfoPO.getConfig());
        return new DefaultKubernetesClient(config);
    }

    public KubernetesClient getAdminKubernetesClientByConfig(String configInfo) {
        Config config = Config.fromKubeconfig(configInfo);
        return new DefaultKubernetesClient(config);
    }

    public KubernetesClient getDefaultClient(){
        return new DefaultKubernetesClient();
    }

}

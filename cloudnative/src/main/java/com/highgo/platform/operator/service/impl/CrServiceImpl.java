/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.highgo.platform.operator.service.impl;

import com.highgo.cloud.constant.DBConstant;
import com.highgo.cloud.constant.OperatorConstant;
import com.highgo.cloud.enums.InstanceStatus;
import com.highgo.cloud.enums.InstanceType;
import com.highgo.cloud.enums.IvoryVersion;
import com.highgo.cloud.enums.SwitchStatus;
import com.highgo.cloud.util.CommonUtil;
import com.highgo.platform.apiserver.model.dto.BackupDTO;
import com.highgo.platform.apiserver.model.dto.BackupPolicyDTO;
import com.highgo.platform.apiserver.model.dto.ConfigInstanceParamDTO;
import com.highgo.platform.apiserver.model.dto.InstanceDTO;
import com.highgo.platform.apiserver.model.vo.request.DatabaseUserVO;
import com.highgo.platform.apiserver.service.BackupService;
import com.highgo.platform.apiserver.service.ConfigService;
import com.highgo.platform.apiserver.service.InstanceService;
import com.highgo.platform.configuration.K8sClientConfiguration;
import com.highgo.platform.errorcode.BackupError;
import com.highgo.platform.errorcode.InstanceError;
import com.highgo.platform.exception.BackupException;
import com.highgo.platform.exception.InstanceException;
import com.highgo.platform.operator.cr.DatabaseCluster;
import com.highgo.platform.operator.cr.bean.DatabaseClusterSpec;
import com.highgo.platform.operator.cr.bean.backup.Restore;
import com.highgo.platform.operator.cr.bean.user.User;
import com.highgo.platform.operator.service.*;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CrServiceImpl implements CrService {

    private static final Logger logger = LoggerFactory.getLogger(CrServiceImpl.class);

    @Resource
    InstanceService instanceService;

    @Resource
    BackupService backupService;

    @Resource
    ConfigService configService;

    @Resource
    OperatorClusterSpecService operatorClusterSpecService;

    @Resource
    K8sClientConfiguration k8sClientConfiguration;

    @Resource
    OperatorCommonService operatorCommonService;

    @Autowired
    OperatorBackupsService operatorBackupsService;

    @Autowired
    OperatorRestoreService operatorRestoreService;
    @Resource
    private OperatorUserService operatorUserService;

    @Value("${cluster.instanceIdName}")
    private String instanceIdName;

    @Value("${cluster.clusterId}")
    private String clusterId;

    @Value("${cluster.prometheusFilter}")
    private String prometheusFilter;

    @Value("${cluster.clusterNameLabel}")
    private String clusterNameLabel;

    @Value("${cluster.clusterRoleLabel}")
    private String clusterRoleLabel;

    @Value("${cluster.crBackupAnnotation}")
    private String crBackupAnnotation;

    @Value("${cluster.crRestoreAnnotation}")
    private String crRestoreAnnotation;

    /**
     * 创建CR，若cr已存在，则退出
     *
     * @param instanceDTO
     * @return
     */
    @Override
    public boolean createCr(InstanceDTO instanceDTO) {
        if (isCrExist(instanceDTO.getClusterId(), instanceDTO.getNamespace(), instanceDTO.getName())) {
            logger.error(
                    "[CrServiceImpl.createCr] cr is exist, will not create and apply. clusterid is {}, namespace is {} name is {}",
                    instanceDTO.getClusterId(), instanceDTO.getNamespace(), instanceDTO.getName());
            return false;
        }
        return applyCr(instanceDTO);
    }

    /**
     * 创建/更新CR,构建整个cr
     *
     * @param instanceDTO
     * @return 执行结果 成功true 失败false
     */
    @Override
    public boolean applyCr(InstanceDTO instanceDTO) {
        // 若出现异常，抛到上层，终止创建实例的事务
        DatabaseCluster databaseCluster = new DatabaseCluster();
        ObjectMeta matedata = new ObjectMeta();
        matedata.setName(instanceDTO.getName());
        // label
        HashMap<String, String> labelsMap = new HashMap<>();
        labelsMap.put(OperatorConstant.OPERATE_LABEL, InstanceStatus.CREATING.name());
        labelsMap.put(instanceIdName, instanceDTO.getId());
        labelsMap.put(clusterId, instanceDTO.getClusterId());
        labelsMap.put(prometheusFilter, instanceDTO.getCreatorName() + instanceDTO.getCreator());

        // 表明是恢复出来的新实例
        if (!StringUtils.isEmpty(instanceDTO.getOriginalInstanceId())
                && !StringUtils.isEmpty(instanceDTO.getOriginalBackupId())) {
            InstanceDTO originInstanceDTO = instanceService.getDTO(instanceDTO.getOriginalInstanceId());
            if (!originInstanceDTO.getNamespace().equals(instanceDTO.getNamespace())
                    || !originInstanceDTO.getClusterId().equals(instanceDTO.getClusterId())) {
                throw new InstanceException(InstanceError.RESTORE_NOT_IN_NAMESPACE);
            }
        }

        if (!StringUtils.isEmpty(instanceDTO.getOriginalInstanceId())) {
            labelsMap.put(OperatorConstant.ORIGIN_INSTANCE_ID, instanceDTO.getOriginalInstanceId());
        }
        if (!StringUtils.isEmpty(instanceDTO.getOriginalBackupId())) {
            labelsMap.put(OperatorConstant.ORIGIN_BACKUP_ID, instanceDTO.getOriginalBackupId());
        }
        matedata.setLabels(labelsMap);
        databaseCluster.setMetadata(matedata);
        // spec
        DatabaseClusterSpec databaseClusterSpec = operatorClusterSpecService.initClusterSpec(instanceDTO);
        databaseCluster.setSpec(databaseClusterSpec);
        // create
        KubernetesClient k8sClient = k8sClientConfiguration.getAdminKubernetesClientById(instanceDTO.getClusterId());
        k8sClient.customResources(DatabaseCluster.class).inNamespace(instanceDTO.getNamespace())
                .createOrReplace(databaseCluster);
        return true;
    }

    /**
     * 校验CR是否已存在
     *
     * @param namespace 命名空间名称
     * @param crName    CR名称
     * @return 存在-true 不存在-false
     */
    @Override
    public boolean isCrExist(String clusterId, String namespace, String crName) {
        KubernetesClient k8sClient = k8sClientConfiguration.getAdminKubernetesClientById(clusterId);
        DatabaseCluster databaseCluster =
                k8sClient.customResources(DatabaseCluster.class).inNamespace(namespace).withName(crName).get();
        if (databaseCluster == null) {
            return false;
        } else {
            return false;
        }
    }

    /**
     * patch cr的resource字段(cpu memory)
     *
     * @param instanceDTO
     * @return
     */
    @Override
    public boolean patchCrResource(InstanceDTO instanceDTO) {
        KubernetesClient kubernetesClient =
                k8sClientConfiguration.getAdminKubernetesClientById(instanceDTO.getClusterId());
        DatabaseCluster databaseCluster = kubernetesClient.customResources(DatabaseCluster.class)
                .inNamespace(instanceDTO.getNamespace()).withName(instanceDTO.getName()).get();
        if (null != instanceDTO.getMemory()) {
            databaseCluster.getSpec().getInstances().get(0).getResources().getLimits()
                    .setMemory(instanceDTO.getMemory() + "Gi");
        }
        if (null != instanceDTO.getCpu()) {
            databaseCluster.getSpec().getInstances().get(0).getResources().getLimits()
                    .setCpu(String.valueOf(instanceDTO.getCpu()));
        }
        databaseCluster.getMetadata().getLabels().put(OperatorConstant.OPERATE_LABEL, InstanceStatus.UPGRADING.name());
        kubernetesClient.customResources(DatabaseCluster.class).inNamespace(instanceDTO.getNamespace())
                .withName(instanceDTO.getName()).patch(databaseCluster);
        return true;
    }

    /**
     * patch cr的storage字段
     *
     * @param instanceDTO
     * @return
     */
    @Override
    public boolean patchCrStorage(InstanceDTO instanceDTO) {
        KubernetesClient kubernetesClient =
                k8sClientConfiguration.getAdminKubernetesClientById(instanceDTO.getClusterId());
        DatabaseCluster databaseCluster = kubernetesClient.customResources(DatabaseCluster.class)
                .inNamespace(instanceDTO.getNamespace()).withName(instanceDTO.getName()).get();
        databaseCluster.getMetadata().getLabels().put(OperatorConstant.OPERATE_LABEL, InstanceStatus.EXTENDING.name());
        databaseCluster.getSpec().getInstances().get(0).getDataVolumeClaimSpec().getResources().getRequests()
                .setStorage(instanceDTO.getStorage() + "Gi");
        kubernetesClient.customResources(DatabaseCluster.class).inNamespace(instanceDTO.getNamespace())
                .withName(instanceDTO.getName()).patch(databaseCluster);
        PersistentVolumeClaimList persistentVolumeClaimList =
                operatorCommonService.getPvcList(kubernetesClient, instanceDTO.getNamespace(), instanceDTO.getName());
        int pvcNum = persistentVolumeClaimList.getItems().size();
        // 启动pvc watch
        Map<String, String> labelFilterMap = operatorCommonService.getLabelSelector(instanceDTO.getName());
        kubernetesClient.persistentVolumeClaims().inNamespace(instanceDTO.getNamespace()).withLabels(labelFilterMap)
                .watch(new Watcher<PersistentVolumeClaim>() {

                    private int readyPvcNum = 0;

                    @Override
                    public void eventReceived(Action action, PersistentVolumeClaim resource) {
                        String storage = resource.getStatus().getCapacity().get("storage").toString();
                        switch (action) {
                            case MODIFIED:
                                if (storage.equals(instanceDTO.getStorage() + "Gi")) {
                                    readyPvcNum += 1;
                                    if (readyPvcNum == pvcNum) {
                                        instanceService.extendInstanceCallback(instanceDTO.getId(), true);
                                        this.onClose();
                                    }
                                }
                            default:
                        }
                    }

                    @Override
                    public void onClose(WatcherException cause) {
                        logger.info("pvc watch closed. instanceId {}", instanceDTO.getId());
                    }
                });
        return true;
    }

    /**
     * 删除CR
     *
     * @param instanceDTO
     * @return 执行结果 成功true 失败false
     */
    @Override
    public boolean deleteCr(InstanceDTO instanceDTO) {
        KubernetesClient k8sClient = k8sClientConfiguration.getAdminKubernetesClientById(instanceDTO.getClusterId());
        k8sClient.customResources(DatabaseCluster.class).inNamespace(instanceDTO.getNamespace())
                .withName(instanceDTO.getName()).delete();
        return true;
    }

    /**
     * 删除实例的所有pod(重启实例时，删除pod进行重启)
     *
     * @param instanceDTO
     * @return
     */
    @Override
    public boolean deleteAllPod(InstanceDTO instanceDTO) {
        KubernetesClient kubernetesClient =
                k8sClientConfiguration.getAdminKubernetesClientById(instanceDTO.getClusterId());
        DatabaseCluster databaseCluster = kubernetesClient.customResources(DatabaseCluster.class)
                .inNamespace(instanceDTO.getNamespace()).withName(instanceDTO.getName()).get();
        databaseCluster.getMetadata().getLabels().put(OperatorConstant.OPERATE_LABEL, InstanceStatus.RESTARTING.name());
        kubernetesClient.customResources(DatabaseCluster.class).inNamespace(instanceDTO.getNamespace())
                .withName(instanceDTO.getName()).patch(databaseCluster);
        Map<String, String> labelFilterMap = operatorCommonService.getLabelSelector(instanceDTO.getName());
        kubernetesClient.pods().inNamespace(instanceDTO.getNamespace()).withLabels(labelFilterMap).delete();
        return true;
    }

    @Override
    public boolean restartDatabase(InstanceDTO instanceDTO) {
        KubernetesClient kubernetesClient =
                k8sClientConfiguration.getAdminKubernetesClientById(instanceDTO.getClusterId());
        DatabaseCluster databaseCluster = kubernetesClient.customResources(DatabaseCluster.class)
                .inNamespace(instanceDTO.getNamespace()).withName(instanceDTO.getName()).get();
        databaseCluster.getMetadata().getLabels().put(OperatorConstant.OPERATE_LABEL, InstanceStatus.RESTARTING.name());
        Map<String, String> annotations = new HashMap<String, String>();
        annotations.put(OperatorConstant.DATABASE_RESTART, "$(date)");
        databaseCluster.getMetadata().setAnnotations(annotations);
        kubernetesClient.customResources(DatabaseCluster.class).inNamespace(instanceDTO.getNamespace())
                .withName(instanceDTO.getName()).patch(databaseCluster);
        return true;
    }

    @Override
    public boolean nodeportSwitch(InstanceDTO instanceDTO) {
        KubernetesClient kubernetesClient =
                k8sClientConfiguration.getAdminKubernetesClientById(instanceDTO.getClusterId());
        DatabaseCluster databaseCluster = kubernetesClient.customResources(DatabaseCluster.class)
                .inNamespace(instanceDTO.getNamespace()).withName(instanceDTO.getName()).get();
        if (SwitchStatus.ON.equals(instanceDTO.getNodePortSwitch())) {
            databaseCluster.getSpec().getService().setType(OperatorConstant.NODEPORT);
            kubernetesClient.customResources(DatabaseCluster.class).inNamespace(instanceDTO.getNamespace())
                    .withName(instanceDTO.getName()).patch(databaseCluster);
            io.fabric8.kubernetes.api.model.Service service = kubernetesClient.services()
                    .inNamespace(instanceDTO.getNamespace()).withName(instanceDTO.getName() + "-ha").get();
            kubernetesClient.services().inNamespace(instanceDTO.getNamespace()).withName(instanceDTO.getName() + "-ha")
                    .watch(new Watcher<io.fabric8.kubernetes.api.model.Service>() {

                        @Override
                        public void eventReceived(Action action, io.fabric8.kubernetes.api.model.Service resource) {
                            Integer nodeport = resource.getSpec().getPorts().get(0).getNodePort();
                            if (nodeport != null) {
                                instanceService.openNodeportSwitchCallback(instanceDTO.getId(), nodeport, null, true);
                                this.onClose();
                            }
                        }

                        @Override
                        public void onClose(WatcherException cause) {
                        }
                    });
        } else {
            databaseCluster.getSpec().getService().setType(OperatorConstant.CLUSTER_IP);
            kubernetesClient.customResources(DatabaseCluster.class).inNamespace(instanceDTO.getNamespace())
                    .withName(instanceDTO.getName()).patch(databaseCluster);
            kubernetesClient.services().inNamespace(instanceDTO.getNamespace()).withName(instanceDTO.getName() + "-ha")
                    .watch(new Watcher<io.fabric8.kubernetes.api.model.Service>() {

                        @Override
                        public void eventReceived(Action action, io.fabric8.kubernetes.api.model.Service resource) {
                            if (OperatorConstant.CLUSTER_IP.equalsIgnoreCase(resource.getSpec().getType())) {
                                instanceService.closeNodeportSwitchCallback(instanceDTO.getId(), true);
                                this.onClose();
                            }
                        }

                        @Override
                        public void onClose(WatcherException cause) {
                        }
                    });
        }
        return true;
    }

    /**
     * 创建备份
     *
     * @param backupDTO
     * @return
     */
    @Override
    public boolean createBackup(BackupDTO backupDTO) {

        InstanceDTO instanceDTO = instanceService.getDTO(backupDTO.getInstanceId());
        KubernetesClient kubernetesClient =
                k8sClientConfiguration.getAdminKubernetesClientById(instanceDTO.getClusterId());

        if (!operatorBackupsService.isBackupInit(kubernetesClient, instanceDTO.getNamespace(), instanceDTO.getName())) {
            throw new BackupException(BackupError.WAIT_BACKUP_INIT);
        }

        io.fabric8.kubernetes.client.dsl.Resource<DatabaseCluster> highgoDBClusterResource =
                kubernetesClient.customResources(DatabaseCluster.class).inNamespace(instanceDTO.getNamespace())
                        .withName(instanceDTO.getName());
        DatabaseCluster databaseCluster = highgoDBClusterResource.get();

        // 状态 BACKUPING备份中
        databaseCluster.getMetadata().getLabels().put(OperatorConstant.OPERATE_LABEL, InstanceStatus.BACKUPING.name());

        Map<String, String> annotations = new HashMap<String, String>();
        annotations.put(crBackupAnnotation, backupDTO.getId());
        databaseCluster.getMetadata().setAnnotations(annotations);
        kubernetesClient.customResources(DatabaseCluster.class).inNamespace(instanceDTO.getNamespace())
                .withName(instanceDTO.getName()).patch(databaseCluster);
        return true;

        // DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");
        // String now = dateFormatter.format(LocalDateTime.now());
        // Map<String, String> annotations = highgoDBCluster.getMetadata().getAnnotations();
        // if(annotations == null){
        // Map<String,String> backupAnnotation = new HashMap();
        // backupAnnotation.put(CommonConstant.CR_BACKUP_ANNOTATION,now);
        // highgoDBCluster.getMetadata().setAnnotations(backupAnnotation);
        // }else{
        // annotations.put(CommonConstant.CR_BACKUP_ANNOTATION,now);
        // }
        // logger.info("Start backup, time:{} ,instanceId:{}", now, instanceDTO.getId());
        // highgoDBClusterResource.patch(highgoDBCluster);
        //
        // Map<String, String> labelFilterMap = new HashMap<>();
        // labelFilterMap.put(CommonConstant.CLUSTER_NAME_LABEL, instanceDTO.getName());
        // labelFilterMap.put(CommonConstant.CR_BACKUP_ANNOTATION, "manual");
        //
        // kubernetesClient
        // .batch()
        // .jobs()
        // .inNamespace(instanceDTO.getNamespace())
        // .withLabels(labelFilterMap)
        // //.withName(instanceDTO.getName() + "-backup")
        // .watch(new Watcher<Job>() {
        // @Override
        // public void eventReceived(Action action, Job job) {
        // io.fabric8.kubernetes.client.dsl.Resource<HighgoDBCluster> highgoDBClusterResource =
        // kubernetesClient.customResources(HighgoDBCluster.class).inNamespace(instanceDTO.getNamespace()).withName(instanceDTO.getName());
        // HighgoDBCluster highgodb = highgoDBClusterResource.get();
        // Map<String, String> jobAnnotations = job.getMetadata().getAnnotations();
        // JobStatus status = job.getStatus();
        // if (jobAnnotations != null && now.equals(jobAnnotations.get(CommonConstant.CR_BACKUP_ANNOTATION))) {
        // if(status != null && !StringUtils.isEmpty(status.getCompletionTime())){
        // //成功
        // if(CommonConstant.JOB_COMPLETE.equalsIgnoreCase(status.getConditions().get(0).getType())){
        //
        // //TODO lcq获取本次备份时间
        //
        // logger.info("Watch backup, backup successed, time:{}, instanceId:{}", now, instanceDTO.getId());
        // operatorCommonService.applyCrLabel(kubernetesClient, highgodb, CommonConstant.OPERATE_LABEL,
        // InstanceStatus.RUNNING.name());
        // backupService.createBackupCallback(backupDTO.getId(), now, true);
        // this.onClose();
        // }
        //
        // //失败
        // if(CommonConstant.JOB_FAILED.equalsIgnoreCase(status.getConditions().get(0).getType())){
        // logger.info("Watch backup, backup failed, time:{}, instanceId:{}", now, instanceDTO.getId());
        // operatorCommonService.applyCrLabel(kubernetesClient, highgodb, CommonConstant.OPERATE_LABEL,
        // InstanceStatus.RUNNING.name());
        // backupService.createBackupCallback(backupDTO.getId(), now, false);
        // this.onClose();
        // }
        // }
        //
        // //失败
        // if(status != null
        // && status.getConditions().size() > 0
        // && CommonConstant.JOB_FAILED.equalsIgnoreCase(status.getConditions().get(0).getType())){
        // logger.info("Watch backup, backup failed, time:{}, instanceId:{}", now, instanceDTO.getId());
        // operatorCommonService.applyCrLabel(kubernetesClient, highgodb, CommonConstant.OPERATE_LABEL,
        // InstanceStatus.RUNNING.name());
        // backupService.createBackupCallback(backupDTO.getId(), now, false);
        // this.onClose();
        // }
        // }
        // }
        //
        // @Override
        // public void onClose(WatcherException cause) {
        // }
        // });
        // return true;
    }

    /**
     * 更新自动备份策略
     *
     * @param backupPolicyDTO
     * @return
     */
    @Override
    public boolean applyBackupPolicy(BackupPolicyDTO backupPolicyDTO) {
        System.out.println("更新自动备份策略中...");
        return true;
    }

    /**
     * 删除备份
     *
     * @param backupDTO
     * @return
     */
    @Override
    public boolean deleteBackup(BackupDTO backupDTO) {
        System.out.println("删除备份中。。。");
        return true;
    }

    @Override
    public boolean applyConfigParam(InstanceDTO instanceDTO) {

        List<ConfigInstanceParamDTO> configInstanceParamDTOS = instanceDTO.getConfigInstanceParamDTOS();

        KubernetesClient kubernetesClient =
                k8sClientConfiguration.getAdminKubernetesClientById(instanceDTO.getClusterId());
        io.fabric8.kubernetes.client.dsl.Resource<DatabaseCluster> highgoDBClusterResource =
                kubernetesClient.customResources(DatabaseCluster.class).inNamespace(instanceDTO.getNamespace())
                        .withName(instanceDTO.getName());
        DatabaseCluster databaseCluster = highgoDBClusterResource.get();
        Map<String, String> runningParams =
                databaseCluster.getSpec().getPatroni().getDynamicConfiguration().getPostgresql().getParameters();

        Map<String, List<ConfigInstanceParamDTO>> changeParams = configInstanceParamDTOS
                .stream()
                .collect(Collectors.groupingBy(p -> p.getName()));

        runningParams.forEach((key, value) -> {
            if (changeParams.keySet().contains(key)) {
                runningParams.put(key, changeParams.get(key).get(0).getValue());
            }
        });

        highgoDBClusterResource.patch(databaseCluster);
        // lcq TODO 修改参数
        highgoDBClusterResource.watch(new Watcher<DatabaseCluster>() {

            @Override
            public void eventReceived(Action action, DatabaseCluster resource) {

            }

            @Override
            public void onClose(WatcherException cause) {

            }
        });

        configService.modifyParametersCallback(instanceDTO.getId(), instanceDTO.getConfigChangeHistoryId(), true);
        return false;
    }

    /**
     * 从CR中解析InstanceDTO
     *
     * @param customResource
     * @return
     */
    @Override
    public InstanceDTO getInstanceVOFromCR(CustomResource customResource) {
        DatabaseCluster databaseCluster = (DatabaseCluster) customResource;
        InstanceDTO instanceDTO = new InstanceDTO();
        ObjectMeta metadata = databaseCluster.getMetadata();
        Map<String, String> labelMap = metadata.getLabels();
        if (labelMap != null) {
            instanceDTO.setClusterId(labelMap.get(clusterId));
            instanceDTO.setId(labelMap.get(instanceIdName));
            instanceDTO.setDescription(StringUtils.isEmpty(labelMap.get(OperatorConstant.DESCRIPTION_NAME))
                    ? OperatorConstant.DEFAULT_CR_DESCRIPTION
                    : labelMap.get(OperatorConstant.DESCRIPTION_NAME));
            instanceDTO.setPassword(
                    StringUtils.isEmpty(labelMap.get(OperatorConstant.PASSWORD)) ? OperatorConstant.DEFAULT_PASSWORD
                            : labelMap.get(OperatorConstant.PASSWORD));
        } else {
            // 反向解析的
            instanceDTO.setDescription(OperatorConstant.DEFAULT_CR_DESCRIPTION);
            instanceDTO.setPassword(OperatorConstant.DEFAULT_PASSWORD);
        }
        instanceDTO.setName(metadata.getName());
        instanceDTO.setNamespace(metadata.getNamespace());
        DatabaseClusterSpec spec = databaseCluster.getSpec();
        if (DBConstant.IVORY_PG_KERNEL_VERSION == spec.getPostgresVersion()) {
            instanceDTO.setVersion(IvoryVersion.IVORY23.getKey());
        }
        instanceDTO.setType(spec.getInstances().get(0).getReplicas() > 1 ? InstanceType.HA : InstanceType.ALONE);
        instanceDTO.setCpu(
                Integer.valueOf(spec.getInstances().get(0).getResources().getLimits().getCpu().replace("c", "")));
        instanceDTO.setMemory(
                Integer.valueOf(spec.getInstances().get(0).getResources().getLimits().getCpu().replace("Gi", "")));
        instanceDTO.setStorage(Integer.valueOf(spec.getInstances().get(0).getDataVolumeClaimSpec().getResources()
                .getRequests().getStorage().replace("Gi", "")));
        instanceDTO.setStorageClass(spec.getInstances().get(0).getDataVolumeClaimSpec().getStorageClassName());
        instanceDTO.setAdmin(spec.getUsers().get(0).getName());
        return instanceDTO;
    }

    @Override
    public Pod getMasterPod(InstanceDTO instanceDTO) {
        KubernetesClient kubernetesClient =
                k8sClientConfiguration.getAdminKubernetesClientById(instanceDTO.getClusterId());
        Map<String, String> labelFilterMap = operatorCommonService.getLabelSelector(instanceDTO.getName());
        List<Pod> items = kubernetesClient.pods().inNamespace(instanceDTO.getNamespace()).withLabels(labelFilterMap)
                .list().getItems();
        return items
                .stream()
                .filter(p -> p.getMetadata().getAnnotations().get("status").contains("\"role\":\"master\""))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("The primary node does not exist"));
    }

    @Override
    public boolean restore(InstanceDTO instanceDTO) {
        // update pgbackrest.restore
        Restore restore = operatorRestoreService.genPgbackuprestRestore(instanceDTO);
        KubernetesClient kubernetesClient =
                k8sClientConfiguration.getAdminKubernetesClientById(instanceDTO.getClusterId());
        DatabaseCluster databaseCluster = kubernetesClient.customResources(DatabaseCluster.class)
                .inNamespace(instanceDTO.getNamespace()).withName(instanceDTO.getName()).get();
        // 状态 RESTORING恢复中
        databaseCluster.getMetadata().getLabels().put(OperatorConstant.OPERATE_LABEL, InstanceStatus.RESTORING.name());
        databaseCluster.getSpec().getBackups().getPgbackrest().setRestore(restore);
        kubernetesClient.customResources(DatabaseCluster.class).inNamespace(instanceDTO.getNamespace())
                .withName(instanceDTO.getName()).patch(databaseCluster);

        // set annotation
        databaseCluster = kubernetesClient.customResources(DatabaseCluster.class)
                .inNamespace(instanceDTO.getNamespace()).withName(instanceDTO.getName()).get();
        Map<String, String> annotations = new HashMap<>();
        // pgbackrest-restore 使用uuid+backupid的方式，否则仅用backupid，用户重复恢复同一个备份不生效
        annotations.put(crRestoreAnnotation,
                String.format("%s:%s", instanceDTO.getOriginalBackupId(), CommonUtil.uuid()));
        databaseCluster.getMetadata().setAnnotations(annotations);
        kubernetesClient.customResources(DatabaseCluster.class).inNamespace(instanceDTO.getNamespace())
                .withName(instanceDTO.getName()).patch(databaseCluster);
        return true;
    }

    @Override
    public Integer getHgadminPort(String instanceId) {
        InstanceDTO dto = instanceService.getDTO(instanceId);
        KubernetesClient kubernetesClient = k8sClientConfiguration.getAdminKubernetesClientById(dto.getClusterId());
        Map<String, String> labelFilterMap = new HashMap<>();
        labelFilterMap.put(clusterNameLabel, dto.getName());
        labelFilterMap.put(clusterRoleLabel, OperatorConstant.PGADMIN);
        List<io.fabric8.kubernetes.api.model.Service> items = kubernetesClient.services()
                .inNamespace(dto.getNamespace()).withLabels(labelFilterMap).list().getItems();
        if (CollectionUtils.isEmpty(items)) {
            throw new RuntimeException("Service of " + instanceId + " pgadmin does not exist.");
        }
        return items
                .get(0)
                .getSpec()
                .getPorts()
                .get(0)
                .getNodePort();

    }

    /**
     * patch cr的resource字段(cpu memory)
     *
     * @param instanceDTO
     * @return
     */
    public boolean patchCrUsers(InstanceDTO instanceDTO, DatabaseUserVO databaseUserVO) {
        KubernetesClient kubernetesClient =
                k8sClientConfiguration.getAdminKubernetesClientById(instanceDTO.getClusterId());
        io.fabric8.kubernetes.client.dsl.Resource<DatabaseCluster> highgoDBClusterResource =
                kubernetesClient.customResources(DatabaseCluster.class).inNamespace(instanceDTO.getNamespace())
                        .withName(instanceDTO.getName());
        DatabaseCluster databaseCluster = highgoDBClusterResource.get();
        List<String> userNames = databaseCluster
                .getSpec()
                .getUsers()
                .stream()
                .map(User::getName)
                .collect(Collectors.toList());

        if (userNames.contains(databaseUserVO.getName())) {
            // TODO 返回 or 抛异常 重名
        }

        List<User> users = databaseCluster.getSpec().getUsers();

        User newUser = User.builder().name(databaseUserVO.getName()).options(databaseUserVO.getOption().name()).build();
        users.add(newUser);
        highgoDBClusterResource.patch(databaseCluster);
        return true;
    }

}

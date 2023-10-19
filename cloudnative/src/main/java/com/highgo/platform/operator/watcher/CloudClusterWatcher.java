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

package com.highgo.platform.operator.watcher;

import com.highgo.cloud.constant.OperatorConstant;
import com.highgo.cloud.enums.AutoScalingStatus;
import com.highgo.cloud.enums.InstanceStatus;
import com.highgo.cloud.util.CommonUtil;
import com.highgo.platform.apiserver.model.dto.InstanceDTO;
import com.highgo.platform.apiserver.model.dto.InstanceNetworkDTO;
import com.highgo.platform.apiserver.model.dto.RestoreRecordDTO;
import com.highgo.platform.apiserver.model.po.InstancePO;
import com.highgo.platform.apiserver.service.AlertAutoScalingService;
import com.highgo.platform.apiserver.service.ExtraMetaService;
import com.highgo.platform.apiserver.service.InstanceService;
import com.highgo.platform.apiserver.service.RestoreRecordService;
import com.highgo.platform.operator.cr.DatabaseCluster;
import com.highgo.platform.operator.cr.bean.status.ManualBackupStatus;
import com.highgo.platform.operator.cr.bean.status.PgbackrestStatus;
import com.highgo.platform.operator.cr.bean.status.RestoreStatus;
import com.highgo.platform.operator.cr.bean.user.User;
import com.highgo.platform.operator.service.*;
import com.highgo.platform.operator.service.impl.OperatorBackupServiceImpl;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.informers.ResourceEventHandler;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@Data
public class CloudClusterWatcher {

    private static final Logger logger = LoggerFactory.getLogger(CloudClusterWatcher.class);
    // private static CloudClusterWatcher cloudClusterWatcher;
    private KubernetesClient kubernetesClient;
    private InstanceService instanceService;
    private OperatorInstanceService operatorInstanceService;
    private OperatorCommonService operatorCommonService;
    private CrService crService;
    private OperatorSvcService operatorSvcService;
    private OperatorUserService operatorUserService;
    private ExtraMetaService extraMetaService;
    private String clusterId;
    private OperatorBackupServiceImpl operatorBackupsService;
    private OperatorRestoreService operatorRestoreService;

    private AlertAutoScalingService alertAutoScalingService;

    private RestoreRecordService restoreRecordService;
    @Value("${cluster.instanceIdName}")
    private String instanceIdName;

    // @PostConstruct
    // public void init() {
    // cloudClusterWatcher = this;
    // }

    public ResourceEventHandler initResourceEventHandler() {
        ResourceEventHandler resourceEventHandler = new ResourceEventHandler<DatabaseCluster>() {

            /**
             * Called when an object is added.
             *
             * @param highgoDBCluster object
             */
            @Override
            public void onAdd(DatabaseCluster highgoDBCluster) {
                ObjectMeta matedata = highgoDBCluster.getMetadata();
                String crName = matedata.getName();
                String namespace = matedata.getNamespace();
                long resourceVersion = Long.valueOf(matedata.getResourceVersion());
                Map<String, String> labelMap = matedata.getLabels();
                String instanceId = "";
                String operateName = "";
                if (labelMap != null) {
                    instanceId = labelMap.get(instanceIdName);
                    operateName = labelMap.get(OperatorConstant.OPERATE_LABEL);
                    // clusterId = labelMap.get(CommonConstant.CLUSTER_ID);
                }

                logger.info(
                        "[ClusterWatcher.onAdd]  cr name is 【{}】, namespace is {}, clusterId is {}, resourceVersion is {}, operate name is {}",
                        crName, namespace, clusterId, resourceVersion, operateName);
                if (StringUtils.isEmpty(instanceId) || labelMap == null) {
                    // 反向解析
                    InstancePO instancePO = new InstancePO();
                    InstanceDTO instanceDTO = crService.getInstanceVOFromCR(highgoDBCluster);
                    instanceDTO.setId(instancePO.getId());
                    instanceDTO.setClusterId(clusterId);
                    logger.info("[ClusterWatcher.onAdd] instanceDTO from cr, {}", instanceDTO.toString());
                    instanceService.createInstance(instanceDTO);
                }
                // 1 resource version 信息入库
                Long resourceVersionDB = instanceService.getResourceVersion(instanceId); // 从数据库中查询该实例resourceversion
                if (resourceVersionDB == null || resourceVersionDB < resourceVersion) {
                    // resourceVersion不存在 或者 实时resourceVersion > 数据库中保存的，执行更新操作
                    instanceService.updateResourseVersion(instanceId, resourceVersion);
                    logger.info(
                            "[ClusterWatcher.onAdd] resource version is updated. crName is 【{}】, namespace is {}, instanceId is {}, resourceVersion is {}, resourceVersionDB is {}, operator name is {}",
                            crName, namespace, instanceId, resourceVersion, resourceVersionDB, operateName);
                } else {
                    logger.warn(
                            "[ClusterWatcher.onAdd] resource version is not updated. crName is 【{}】, namespace is {}, instanceId is {}, resourceVersion is {}, resourceVersionDB is {}, operator name is {}",
                            crName, namespace, instanceId, resourceVersion, resourceVersionDB, operateName);
                }
            }

            /**
             * Called when an object is modified. Note that oldObj is the last
             * known state of the object -- it is possible that several changes
             * were combined together, so you can't use this to see every single
             * change. It is also called when a re-list happens, and it will get
             * called even if nothing changes. This is useful for periodically
             * evaluating or syncing something.
             *
             * @param oldCluster old object
             * @param newCluster new object
             */
            @Override
            public void onUpdate(DatabaseCluster oldCluster, DatabaseCluster newCluster) {
                // 获取cr信息
                ObjectMeta matedata = newCluster.getMetadata();
                String crName = matedata.getName();
                String namespace = matedata.getNamespace();
                long resourceVersion = Long.valueOf(matedata.getResourceVersion());
                Map<String, String> labelMap = matedata.getLabels();
                String instanceId = labelMap.get(instanceIdName);
                String operateName = labelMap.get(OperatorConstant.OPERATE_LABEL);
                String resourceVersionOld = oldCluster.getMetadata().getResourceVersion();
                Integer nodeNum = operatorInstanceService.getNodeNum(newCluster); // 实例节点总数
                Integer nodeReadyNum = operatorInstanceService.getNodeReadyNum(newCluster); // 实例ready节点数量
                boolean isCrReady = false;
                if (nodeNum != null && nodeNum.equals(nodeReadyNum)) {
                    isCrReady = true;
                }
                Long resourceVersionDB = instanceService.getResourceVersion(instanceId);
                if (resourceVersionDB == null) {
                    resourceVersionDB = resourceVersion;
                    instanceService.updateResourseVersion(instanceId, resourceVersion);
                }
                // 获取数据库中实例状态
                InstanceStatus instanceStatus = instanceService.getInstanceStatus(instanceId);
                logger.info(
                        "[ClusterWatcher.onUpdate] cr name 【{}】, namespace {}, clusterId {}, instanceId {}, newVersion {}, oldversion {} dbversion {}, node num {}, node ready num {}, isCrready {}, instanceStatus {}, operate {}",
                        crName, namespace, clusterId, instanceId, resourceVersion, resourceVersionOld,
                        resourceVersionDB, nodeNum, nodeReadyNum, isCrReady, instanceStatus, operateName);
                if (InstanceStatus.DELETED.equals(instanceStatus)) {
                    logger.warn(
                            "[ClusterWatcher.onUpdate] instance was deleted, no need update. cr name 【{}】, namespace {}, clusterId {}, instanceId {}",
                            crName, namespace, clusterId, instanceId);
                    return;
                }
                InstanceDTO instanceDTO = instanceService.getDTO(instanceId);
                operateTimeoutHandler(instanceDTO, newCluster);
                if (resourceVersion < resourceVersionDB) {
                    // 已处理的事件
                    logger.info(
                            "[ClusterWatcher.onUpdate] cr name 【{}】, namespace {}, clusterId {}, instanceId {}, resource version[{}] < resourve version db[{}]",
                            crName, namespace, clusterId, instanceId, resourceVersion, resourceVersionDB);
                    return;
                }
                instanceService.updateResourseVersion(instanceId, resourceVersion); // 更新表resourceversion
                if (resourceVersion == resourceVersionDB) {
                    operatorCommonService.saveEvent(kubernetesClient, instanceId, namespace, crName, nodeReadyNum);
                    logger.info(
                            "[ClusterWatcher.onUpdate] cr name 【{}】, namespace {}, clusterId {}, instanceId {}, resource version[{}] = resourve version db[{}]",
                            crName, namespace, clusterId, instanceId, resourceVersion, resourceVersionDB);
                } else {
                    // resource version > resource version db
                    instanceService.updateResourseVersion(instanceId, resourceVersion); // 更新表resourceversion
                    operatorCommonService.saveEvent(kubernetesClient, instanceId, namespace, crName, nodeReadyNum);
                    // 判断事件类型，执行对应逻辑
                    logger.info(
                            "[ClusterWatcher.onUpdate] cr name 【{}】, namespace {}, clusterId {}, instanceId {}, resource version[{}] > resourve version db[{}]",
                            crName, namespace, clusterId, instanceId, resourceVersion, resourceVersionDB);

                    newUpdateEventHandler(isCrReady, nodeReadyNum, instanceStatus, newCluster, oldCluster);
                    backupEventHandler(instanceDTO, newCluster);
                    restoreEventHandler(instanceId, newCluster, instanceStatus);
                }
            }

            /**
             * Gets the final state of the item if it is known, otherwise
             * it would get an object of the DeletedFinalStateUnknown. This can
             * happen if the watch is closed and misses the delete event and
             * we don't notice the deletion until the subsequent re-list.
             *
             * @param highgoDBCluster                      object to delete
             * @param deletedFinalStateUnknown get final state of item if it is known or not.
             */
            @Override
            public void onDelete(DatabaseCluster highgoDBCluster, boolean deletedFinalStateUnknown) {
                ObjectMeta matedata = highgoDBCluster.getMetadata();
                String crName = matedata.getName();
                String namespace = matedata.getNamespace();
                Map<String, String> labelMap = matedata.getLabels();
                String instanceId = labelMap.get(instanceIdName);
                logger.info("[ClusterWatcher.onDelete] cr is deleted! cr name 【{}】, namespae {}, instanceId {}", crName,
                        namespace, instanceId);
                instanceService.deleteInstanceCallback(instanceId, true);
            }
        };
        return resourceEventHandler;
    }

    private void newUpdateEventHandler(boolean isCrReady, Integer nodeReadyNum, InstanceStatus instanceStatus,
            DatabaseCluster newDatabaseCluster, DatabaseCluster oldDatabaseCluster) {
        ObjectMeta matedata = newDatabaseCluster.getMetadata();
        String crName = matedata.getName();
        String namespace = matedata.getNamespace();
        Map<String, String> labelMap = matedata.getLabels();
        String instanceId = labelMap.get(instanceIdName);
        String originInstanceId = labelMap.get(OperatorConstant.ORIGIN_INSTANCE_ID);
        String originBackupId = labelMap.get(OperatorConstant.ORIGIN_BACKUP_ID);
        List<User> users = newDatabaseCluster.getSpec().getUsers();
        if (isCrReady) {
            Integer oldReadyNum = oldDatabaseCluster.getStatus().getInstances().get(0).getReadyReplicas();
            switch (instanceStatus) {
                case CREATING:
                case CREATE_FAILED:
                    logger.info(
                            "[ClusterWatcher.onUpdate] cr name 【{}】, namespace {}, clusterId {}, instanceId {}, creating --> running ",
                            crName, namespace, clusterId, instanceId);
                    List<InstanceNetworkDTO> networkDTOList = operatorSvcService
                            .getInstanceNetworkDTOList(kubernetesClient, instanceId, namespace, crName);
                    instanceService.createInstanceCallback(instanceId, networkDTOList, originInstanceId, originBackupId,
                            true);
                    operatorCommonService.applyCrLabel(kubernetesClient, newDatabaseCluster,
                            OperatorConstant.OPERATE_LABEL, InstanceStatus.RUNNING.name());
                    users
                            .stream()
                            .forEach(u -> operatorUserService.resetPassword(clusterId, namespace, crName, u.getName(),
                                    CommonUtil.unBase64(extraMetaService
                                            .findExtraMetaByInstanceIdAndName(instanceId, OperatorConstant.PASSWORD)
                                            .get().getValue())));
                    extraMetaService.deleteByInstanceIdAndName(instanceId, OperatorConstant.PASSWORD);
                    break;
                case UPGRADING:
                    if (nodeReadyNum.equals(oldReadyNum)
                            || !operatorInstanceService.isAllPodRebuild(clusterId, namespace, crName, instanceId)) {
                        // pod数量无变化，说明operator还未开始执行任务
                        // 非所有pod已经重建，不做处理
                        break;
                    }
                    logger.info(
                            "[ClusterWatcher.onUpdate] cr name 【{}】, namespace {}, clusterId {}, instanceId {} upgrading --> running",
                            crName, namespace, clusterId, instanceId);
                    instanceService.modifyInstanceCallback(instanceId, true);
                    operatorCommonService.applyCrLabel(kubernetesClient, newDatabaseCluster,
                            OperatorConstant.OPERATE_LABEL, InstanceStatus.RUNNING.name());
                    break;
                case AUTO_SCALING:
                    if (nodeReadyNum.equals(oldReadyNum)
                            || !operatorInstanceService.isAllPodRebuild(clusterId, namespace, crName, instanceId)) {
                        // pod数量无变化，说明operator还未开始执行任务
                        // 非所有pod已经重建，不做处理
                        break;
                    }
                    logger.info(
                            "[ClusterWatcher.onUpdate] cr name 【{}】, namespace {}, clusterId {}, instanceId {} autoscaling --> running",
                            crName, namespace, clusterId, instanceId);
                    instanceService.updateInstanceStatus(instanceId, InstanceStatus.RUNNING);
                    alertAutoScalingService.autoScalingOperatorCallBack(instanceId, AutoScalingStatus.SUCCESS,
                            "autoscaling success");
                    operatorCommonService.applyCrLabel(kubernetesClient, newDatabaseCluster,
                            OperatorConstant.OPERATE_LABEL, InstanceStatus.RUNNING.name());
                    break;
                case UPGRADE_FLAVOR_FAILED:
                    if (nodeReadyNum.equals(oldReadyNum)) {
                        // pod数量无变化，说明operator还未开始执行任务
                        logger.info(
                                "[ClusterWatcher.onUpdate] cr name 【{}】, namespace {}, clusterId {}, instanceId {} upgrade flavor failed break",
                                crName, namespace, clusterId, instanceId);
                        break;
                    }
                    logger.info(
                            "[ClusterWatcher.onUpdate] cr name 【{}】, namespace {}, clusterId {}, instanceId {} upgrading --> running",
                            crName, namespace, clusterId, instanceId);
                    instanceService.modifyInstanceCallback(instanceId, true);
                    operatorCommonService.applyCrLabel(kubernetesClient, newDatabaseCluster,
                            OperatorConstant.OPERATE_LABEL, InstanceStatus.RUNNING.name());
                    break;
                case RESTARTING:
                    if (nodeReadyNum.equals(oldReadyNum)) {
                        break;
                    }
                    logger.info(
                            "[ClusterWatcher.onUpdate] cr name 【{}】, namespace {}, clusterId {}, instanceId {} restarting --> running",
                            crName, namespace, clusterId, instanceId);
                    instanceService.restartInstanceCallback(instanceId, true);
                    operatorCommonService.applyCrLabel(kubernetesClient, newDatabaseCluster,
                            OperatorConstant.OPERATE_LABEL, InstanceStatus.RUNNING.name());
                    break;
                case RESTART_FAILED:
                    if (nodeReadyNum.equals(oldReadyNum)) {
                        logger.info(
                                "[ClusterWatcher.onUpdate] cr name 【{}】, namespace {}, clusterId {}, instanceId {} restart failed break",
                                crName, namespace, clusterId, instanceId);
                        break;
                    }
                    logger.info(
                            "[ClusterWatcher.onUpdate] cr name 【{}】, namespace {}, clusterId {}, instanceId {} restartfailed --> running",
                            crName, namespace, clusterId, instanceId);
                    instanceService.restartInstanceCallback(instanceId, true);
                    operatorCommonService.applyCrLabel(kubernetesClient, newDatabaseCluster,
                            OperatorConstant.OPERATE_LABEL, InstanceStatus.RUNNING.name());
                    break;
                case CONFIG_CHANGING:
                    if (nodeReadyNum.equals(oldReadyNum)) {
                        break;
                    }
                    logger.info(
                            "[ClusterWatcher.onUpdate] cr name 【{}】, namespace {}, clusterId {}, instanceId {} config_changing --> running",
                            crName, namespace, clusterId, instanceId);
                    break;
                case ERROR:
                    logger.info(
                            "[ClusterWatcher.onUpdate] cr name 【{}】, namespace {}, clusterId {}, instanceId {} error --> running",
                            crName, namespace, clusterId, instanceId);
                    instanceService.updateInstanceStatus(instanceId, InstanceStatus.RUNNING);
                    operatorCommonService.applyCrLabel(kubernetesClient, newDatabaseCluster,
                            OperatorConstant.OPERATE_LABEL, InstanceStatus.RUNNING.name());
                    break;
                default:
                    logger.info(
                            "[ClusterWatcher.onUpdate] cr name 【{}】, namespace {}, clusterId {}, instanceId {}, {} --> ",
                            crName, namespace, clusterId, instanceId, instanceStatus);
            }
        } else {
            switch (instanceStatus) {
                case RUNNING:
                    // 常规运行中
                    logger.warn(
                            "[ClusterWatcher.onUpdate] cr name 【{}】, namespace {}, clusterId {}, instanceId {}, running --> error",
                            crName, namespace, clusterId, instanceId);
                    instanceService.updateInstanceStatus(instanceId, InstanceStatus.ERROR);
                    operatorCommonService.applyCrLabel(kubernetesClient, newDatabaseCluster,
                            OperatorConstant.OPERATE_LABEL, InstanceStatus.ERROR.name());
                    break;
                default:
                    // 数据库已更新为操作中
                    logger.info(
                            "[ClusterWatcher.onUpdate] cr is not ready yet. cr name 【{}】, namespace {}, clusterId {}, instanceId {}",
                            crName, namespace, clusterId, instanceId);
            }
        }
    }

    private void backupEventHandler(InstanceDTO instanceDTO, DatabaseCluster newDatabaseCluster) {
        // 备份事件
        if (newDatabaseCluster.getStatus() == null || newDatabaseCluster.getStatus().getPgbackrest() == null) {
            // 无备份事件产生
            return;
        }
        PgbackrestStatus newPgbackrestStatus = newDatabaseCluster.getStatus().getPgbackrest();
        // 手动备份事件，提交manualBackupEventHandler处理
        manualBackupEventHandler(newPgbackrestStatus.getManualBackup());
        // 修改cr状态
        if (newPgbackrestStatus.getManualBackup() == null) {
            return;
        }
        if (newPgbackrestStatus.getManualBackup().getFinished()) {
            if (newPgbackrestStatus.getManualBackup().getSucceeded() != null
                    && newPgbackrestStatus.getManualBackup().getSucceeded() > 0) {
                operatorCommonService.applyCrLabel(kubernetesClient, newDatabaseCluster, OperatorConstant.OPERATE_LABEL,
                        InstanceStatus.RUNNING.name());
            } else {
                operatorCommonService.applyCrLabel(kubernetesClient, newDatabaseCluster, OperatorConstant.OPERATE_LABEL,
                        InstanceStatus.ERROR.name());
            }
        }

    }

    private void manualBackupEventHandler(ManualBackupStatus newManualBackupStatus) {
        if (newManualBackupStatus == null) {
            return;
        }
        operatorBackupsService.syncManualBackup(newManualBackupStatus);
    }

    // private void autoBackupEventHandler(String instanceId, List<ScheduledBackupsStatus>
    // newScheduledBackupsStatusList) {
    // if (newScheduledBackupsStatusList == null) {
    // return;
    // }
    // operatorBackupsService.syncAutoBackup(instanceId, newScheduledBackupsStatusList);
    // }

    private void restoreEventHandler(String instanceId, DatabaseCluster databaseCluster,
            InstanceStatus instanceStatus) {
        if (!InstanceStatus.RESTORING.equals(instanceStatus)) {
            // 当前实例不是恢复中状态，restorehandler不进行处理
            return;
        }
        if (databaseCluster.getStatus() == null || databaseCluster.getStatus().getPgbackrest().getRestore() == null) {
            return;
        }
        RestoreStatus restore = databaseCluster.getStatus().getPgbackrest().getRestore();
        if (restore == null) {
            return;
        }

        // 查询恢复记录表中的数据
        RestoreRecordDTO restoreRecord = restoreRecordService.getRestoreRecordByInstanceId(instanceId);
        // 如果当前数据与库里数据一样，不做操作。（说明当前结果是上一次备份的信息，不能只根据finished状态判断）
        // 如果都是空或者数据相同，视为相同
        if (restoreRecord != null) {
            if (((restoreRecord.getStartTime() == null && restore.getStartTime() == null)
                    || (restoreRecord.getStartTime() != null
                            && restoreRecord.getStartTime().equals(restore.getStartTime())))
                    && ((restoreRecord.getCompletionTime() == null && restore.getCompletionTime() == null)
                            || (restoreRecord.getCompletionTime() != null
                                    && restoreRecord.getCompletionTime().equals(restore.getCompletionTime())))) {
                return;
            }
        }
        if (restore.getFailed() != null && restore.getFailed() > 0) {
            operatorRestoreService.restoreCallback(kubernetesClient, instanceId, restore, restoreRecord);
            operatorCommonService.applyCrLabel(kubernetesClient, databaseCluster, OperatorConstant.OPERATE_LABEL,
                    InstanceStatus.RUNNING.name());
        }

        if (restore.getFinished() != null && !restore.getFinished()) {
            // 恢复未完成
            return;
        }
        // if (restore.getId().startsWith("~pgo-bootstrap")) {
        // // 恢复到新实例，newUpdateEventHandler中的CREATING处回调callback
        // return;
        // }

        // String originInstanceId = highgoDBCluster.getMetadata().getLabels().get(OperatorConstant.ORIGIN_INSTANCE_ID);
        // String originBackupId = highgoDBCluster.getMetadata().getLabels().get(OperatorConstant.ORIGIN_BACKUP_ID);
        // String namespace = highgoDBCluster.getMetadata().getNamespace();
        // String name = highgoDBCluster.getMetadata().getName();
        // List<User> users = highgoDBCluster.getSpec().getUsers();
        // users
        // .stream()
        // .forEach(u -> operatorUserService.resetPassword(clusterId, namespace, name, u.getName(),
        // CommonUtils.unBase64(extraMetaService.findExtraMetaByInstanceIdAndName(instanceId,
        // OperatorConstant.PASSWORD).get().getValue())));
        //
        // extraMetaService.deleteByInstanceIdAndName(instanceId, OperatorConstant.PASSWORD);
        // List<InstanceNetworkDTO> networkDTOList = operatorSvcService.getInstanceNetworkDTOList(kubernetesClient,
        // instanceId, namespace, name);
        operatorRestoreService.restoreCallback(kubernetesClient, instanceId, restore, restoreRecord);
        // instanceService.createInstanceCallback(instanceId, networkDTOList, originInstanceId, originBackupId,
        // restore.getFinished() && restore.getSucceeded() > 0);
        // instanceService.createInstanceHgadminCallback(instanceId);
        operatorCommonService.applyCrLabel(kubernetesClient, databaseCluster, OperatorConstant.OPERATE_LABEL,
                InstanceStatus.RUNNING.name());
    }

    /**
     * description: 状态超时处理
     * date: 2023/3/20 16:32
     * @param instanceDTO
     * @param databaseCluster
     * @return: void
     * @author: highgo-lucunqiao
     * @since JDK 1.8
     */
    private void operateTimeoutHandler(InstanceDTO instanceDTO, DatabaseCluster databaseCluster) {
        ObjectMeta matedata = databaseCluster.getMetadata();
        Map<String, String> labelMap = matedata.getLabels();
        String instanceId = labelMap.get(instanceIdName);
        String originInstanceId = labelMap.get(OperatorConstant.ORIGIN_INSTANCE_ID);
        String originBackupId = labelMap.get(OperatorConstant.ORIGIN_BACKUP_ID);
        InstanceStatus instanceStatus = instanceDTO.getStatus();
        if (CommonUtil.getUTCDate().getTime() - instanceDTO.getUpdatedAt().getTime() < TimeUnit.MINUTES.toMillis(10)) {
            return;
        }
        switch (instanceStatus) {
            case CREATING:
                instanceService.createInstanceCallback(instanceDTO.getId(), new ArrayList<>(), originInstanceId,
                        originBackupId, false);
                break;
            case BACKUPING:
                // TODO 暂时不处理备份， 不确定用户备份需要时间
                // operatorBackupsService.backupTimeoutHandler(instanceId);
                break;
            case UPGRADING:
                instanceService.modifyInstanceCallback(instanceId, false);
                break;
            case RESTARTING:
                instanceService.restartInstanceCallback(instanceId, false);
                break;
            case DELETING:
                instanceService.deleteInstanceCallback(instanceId, false);
                break;
            default:
                break;
        }
    }
}

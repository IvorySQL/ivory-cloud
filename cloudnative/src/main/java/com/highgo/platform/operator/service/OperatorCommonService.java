package com.highgo.platform.operator.service;

import com.highgo.platform.operator.cr.DatabaseCluster;
import com.highgo.platform.operator.cr.bean.common.VolumeClaimSpec;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimList;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.apps.StatefulSetList;
import io.fabric8.kubernetes.client.KubernetesClient;

import java.util.Map;

public interface OperatorCommonService {

    /**
     * 构建存储对象
     *
     * @param storage          存储磁盘大小 正整数
     * @param storageClassName 存储类型名称 e.g incloud-lvm/local-path...
     * @return
     */
    public VolumeClaimSpec getVolumeClaimSpec(String storage, String storageClassName);

    /**
     * 更新/创建label
     *
     * @param databaseCluster
     * @param labelName
     * @param labelValue
     */
    public void applyCrLabel(KubernetesClient kubernetesClient, DatabaseCluster databaseCluster, String labelName, String labelValue);

    /**
     * 更新、创建label
     *
     * @param kubernetesClient
     * @param labelMap
     */
    public void applyCrLabel(KubernetesClient kubernetesClient, DatabaseCluster databaseCluster, Map<String, String> labelMap);

    /**
     * 获取实例的event信息并入库
     *
     * @param kubernetesClient
     * @param namespace
     * @param crName
     */
    public void saveEvent(KubernetesClient kubernetesClient, String instanceId, String namespace, String crName, Integer nodeReadyNum);

    /**
     * 获取pod列表
     *
     * @param namespace
     * @param crName
     * @return
     */
    public PodList getPodList(KubernetesClient kubernetesClient, String namespace, String crName);

    /**
     * 获取statefulset 列表
     *
     * @param namespace
     * @param crName
     * @return
     */
    public StatefulSetList getStsList(KubernetesClient kubernetesClient, String namespace, String crName);

    /**
     * 获取pvc列表
     *
     * @param namespace
     * @param crName
     * @return
     */
    public PersistentVolumeClaimList getPvcList(KubernetesClient kubernetesClient, String namespace, String crName);

    /**
     * 获取实例的标签选择器
     * @param instanceName
     * @return
     */
    public Map<String, String> getLabelSelector(String instanceName);
}

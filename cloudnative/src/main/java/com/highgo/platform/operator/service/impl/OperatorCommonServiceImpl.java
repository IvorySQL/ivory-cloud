package com.highgo.platform.operator.service.impl;

import com.highgo.platform.apiserver.service.InstanceService;
import com.highgo.platform.configuration.K8sClientConfiguration;
import com.highgo.cloud.constant.OperatorConstant;
import com.highgo.platform.operator.cr.DatabaseCluster;
import com.highgo.platform.operator.cr.bean.common.StorageRequests;
import com.highgo.platform.operator.cr.bean.common.StorageResource;
import com.highgo.platform.operator.cr.bean.common.VolumeClaimSpec;
import com.highgo.platform.operator.service.OperatorCommonService;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimList;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.api.model.apps.StatefulSetList;
import io.fabric8.kubernetes.api.model.events.v1beta1.Event;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OperatorCommonServiceImpl implements OperatorCommonService {
    private static final Logger logger = LoggerFactory.getLogger(OperatorCommonServiceImpl.class);

    @Autowired
    InstanceService instanceService;

    @Autowired
    K8sClientConfiguration k8sClientConfiguration;

    @Value("${cluster.clusterNameLabel}")
    private String clusterNameLabel;

    @Value("${cluster.clusterDataLabel}")
    private String clusterDataLabel;
    /**
     * 构建存储对象
     *
     * @param storage          存储磁盘大小 正整数
     * @param storageClassName 存储类型名称 e.g incloud-lvm/local-path...
     * @return
     */
    @Override
    public VolumeClaimSpec getVolumeClaimSpec(String storage, String storageClassName) {
        VolumeClaimSpec volumeClaimSpec = new VolumeClaimSpec();
        StorageRequests storageRequests = StorageRequests.builder().storage(storage).build();
        StorageResource storageResource = StorageResource.builder().requests(storageRequests).build();
        volumeClaimSpec.setResources(storageResource);
        volumeClaimSpec.setStorageClassName(storageClassName);
        return volumeClaimSpec;
    }

    /**
     * 更新/创建label
     *
     * @param databaseCluster
     * @param labelName
     * @param labelValue
     */
    @Override
    public void applyCrLabel(KubernetesClient kubernetesClient, DatabaseCluster databaseCluster, String labelName, String labelValue) {
        ObjectMeta matedata = databaseCluster.getMetadata();
        Map<String, String> labelMap = matedata.getLabels();
        labelMap.put(labelName, labelValue);
        databaseCluster.getMetadata().setLabels(labelMap);
        kubernetesClient.customResources(DatabaseCluster.class).inNamespace(databaseCluster.getMetadata().getNamespace()).replace(databaseCluster);
    }

    /**
     * 更新、创建label
     *
     * @param kubernetesClient
     * @param labelMapPatch
     */
    @Override
    public void applyCrLabel(KubernetesClient kubernetesClient, DatabaseCluster databaseCluster, Map<String, String> labelMapPatch) {
        ObjectMeta matedata = databaseCluster.getMetadata();
        Map<String, String> labelMap = matedata.getLabels();
        for (String key : labelMapPatch.keySet()) {
            labelMap.put(key, labelMapPatch.get(key));
        }
        databaseCluster.getMetadata().setLabels(labelMap);
        kubernetesClient.customResources(DatabaseCluster.class).replace(databaseCluster);
    }

    /**
     * 获取实例的event信息并入库
     *
     * @param kubernetesClient
     * @param namespace
     * @param crName
     */
    @Override
    public void saveEvent(KubernetesClient kubernetesClient, String instanceId, String namespace, String crName, Integer nodeReadyNum) {
        // sts 事件
//        logger.info("[OperatorCommonServiceImpl.saveEvent] crName 【{}】, namespace {}, instanceId {}, get event and save ...", crName, namespace, instanceId);
        saveStsEvent(kubernetesClient, instanceId, namespace, crName);
        savePodEvent(kubernetesClient, instanceId, namespace, crName);
        if (nodeReadyNum != null) {
            instanceService.updateNodeReadyNum(instanceId, nodeReadyNum);
//            logger.info("[OperatorCommonServiceImpl.saveEvent] save node ready number success. namespace {}, crName 【{}】, instanceId {}, nodeReadyNum {}", namespace, crName, instanceId, nodeReadyNum);
        }


    }

    private void saveStsEvent(KubernetesClient kubernetesClient, String instanceId, String namespace, String crName) {
        try {
            Map<String, String> labelFilterMap = new HashMap<>();
            labelFilterMap.put(clusterNameLabel, crName);
            labelFilterMap.put(clusterDataLabel, "postgres");
            StatefulSetList statefulSetList = kubernetesClient.apps().statefulSets().inNamespace(namespace).withLabels(labelFilterMap).list();
            List<Event> stsEvents = new ArrayList<>();
            for (StatefulSet statefulSet : statefulSetList.getItems()) {
                Map<String, String> stsFieldSelector = new HashMap<>();
                stsFieldSelector.put("regarding.name", statefulSet.getMetadata().getName());
                stsFieldSelector.put("regarding.kind", "StatefulSet");
                stsEvents.addAll(kubernetesClient.events().v1beta1().events().inNamespace(namespace).withFields(stsFieldSelector).list().getItems());
            }
            String lastStsEventTimeStamp = null;
            Event stsEvent = null;
            for (Event tempEvent : stsEvents) {
                String tempStsEventTimeStamp = StringUtils.isEmpty(tempEvent.getDeprecatedLastTimestamp()) ? tempEvent.getEventTime().getTime() : tempEvent.getDeprecatedLastTimestamp();
                if (lastStsEventTimeStamp == null || tempStsEventTimeStamp.compareTo(lastStsEventTimeStamp) > 0) {
                    lastStsEventTimeStamp = tempStsEventTimeStamp;
                    stsEvent = tempEvent;
                }
            }
            if (stsEvent != null) {
                instanceService.updateStsEvent(instanceId, stsEvent.getNote());
//                logger.info("[OperatorCommonServiceImpl.saveStsEvent] save sts event success. namespace {}, crName {}, instanceId {}, statefulset event {}", namespace, crName, instanceId, stsEvent.getNote());
            }
        } catch (Exception e) {
            logger.info("[OperatorCommonServiceImpl.saveStsEvent] save sts event faile. namespace {}, crName {}, instanceId {}", namespace, crName, instanceId);
        }

    }

    private void savePodEvent(KubernetesClient kubernetesClient, String instanceId, String namespace, String crName) {
        try {
            PodList podList = getPodList(kubernetesClient, namespace, crName);
            List<Event> podEvents = new ArrayList<>();
            for (Pod pod : podList.getItems()) {
                Map<String, String> stsFieldSelector = new HashMap<>();
                stsFieldSelector.put("regarding.name", pod.getMetadata().getName());
                stsFieldSelector.put("regarding.kind", "Pod");
                podEvents.addAll(kubernetesClient.events().v1beta1().events().inNamespace(namespace).withFields(stsFieldSelector).list().getItems());
            }
            String lastPodEventTimeStamp = null;
            Event podEvent = null;
            for (Event tempEvent : podEvents) {
                String tempPodEventTimeStamp = StringUtils.isEmpty(tempEvent.getDeprecatedLastTimestamp()) ? tempEvent.getEventTime().getTime() : tempEvent.getDeprecatedLastTimestamp();
                if (lastPodEventTimeStamp == null || tempPodEventTimeStamp.compareTo(lastPodEventTimeStamp) > 0) {
                    lastPodEventTimeStamp = tempPodEventTimeStamp;
                    podEvent = tempEvent;
                }
            }
            if (podEvent != null) {
                String podEventStr = podEvent.getNote().split(":")[0];
                instanceService.updatePodEvent(instanceId, podEventStr);
//                logger.info("[OperatorCommonServiceImpl.savePodEvent] save pod event success. namespace {}, crName {}, instanceId {}, statefulset event {}", namespace, crName, instanceId, podEventStr);
            }
        } catch (Exception e) {
            logger.info("[OperatorCommonServiceImpl.savePodEvent] save pod event failed. namespace {}, crName {}, instanceId {}", namespace, crName, instanceId);
        }

    }

    /**
     * 获取pod列表
     *
     * @param namespace
     * @param crName
     * @return
     */
    @Override
    public PodList getPodList(KubernetesClient kubernetesClient, String namespace, String crName) {
        Map<String, String> labelFilterMap = getLabelSelector(crName);
        PodList podList = kubernetesClient.pods().inNamespace(namespace).withLabels(labelFilterMap).list();
        return podList;
    }

    /**
     * 获取statefulset 列表
     *
     * @param namespace
     * @param crName
     * @return
     */
    @Override
    public StatefulSetList getStsList(KubernetesClient kubernetesClient, String namespace, String crName) {
        Map<String, String> labelFilterMap = getLabelSelector(crName);
        StatefulSetList statefulSetList = kubernetesClient.apps().statefulSets().inNamespace(namespace).withLabels(labelFilterMap).list();
        return statefulSetList;
    }

    /**
     * 获取pvc列表
     *
     * @param namespace
     * @param crName
     * @return
     */
    @Override
    public PersistentVolumeClaimList getPvcList(KubernetesClient kubernetesClient, String namespace, String crName) {
        Map<String, String> labelFilterMap = getLabelSelector(crName);
        PersistentVolumeClaimList persistentVolumeClaimList = kubernetesClient.persistentVolumeClaims().inNamespace(namespace).withLabels(labelFilterMap).list();
        return persistentVolumeClaimList;
    }

    /**
     * 获取实例的标签选择器
     *
     * @param instanceName
     * @return
     */
    @Override
    public Map<String, String> getLabelSelector(String instanceName) {
        Map<String, String> labelFilterMap = new HashMap<>();
        labelFilterMap.put(clusterNameLabel, instanceName);
        labelFilterMap.put(clusterDataLabel, "postgres");
        return labelFilterMap;
    }
}

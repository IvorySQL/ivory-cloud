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

import com.highgo.platform.apiserver.model.dto.InstanceDTO;
import com.highgo.platform.apiserver.service.InstanceService;
import com.highgo.platform.configuration.K8sClientConfiguration;
import com.highgo.platform.operator.cr.DatabaseCluster;
import com.highgo.platform.operator.cr.bean.DatabaseClusterStatus;
import com.highgo.platform.operator.cr.bean.common.Limit;
import com.highgo.platform.operator.cr.bean.common.Request;
import com.highgo.platform.operator.cr.bean.common.Resource;
import com.highgo.platform.operator.cr.bean.instance.Instance;
import com.highgo.platform.operator.cr.bean.instance.StatusInstance;
import com.highgo.platform.operator.service.OperatorCommonService;
import com.highgo.platform.operator.service.OperatorInstanceService;
import com.highgo.cloud.util.CommonUtil;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OperatorInstanceServiceImpl implements OperatorInstanceService {

    @Autowired
    private OperatorCommonService operatorCommonService;

    @Autowired
    private InstanceService instanceService;

    @Autowired
    private K8sClientConfiguration k8sClientConfiguration;

    /**
     * 构建cr instance节点
     *
     * @param name     实例名称
     * @param replicas 实例副本数
     * @param cpu      cpu配置
     * @param memory   内存配置
     * @param storage  磁盘配置
     * @return
     */
    @Override
    public Instance geInstance(String name, int replicas, int cpu, int memory, String storage, String storageClass) {
        Instance instance = new Instance();
        instance.setReplicas(replicas);
        instance.setDataVolumeClaimSpec(operatorCommonService.getVolumeClaimSpec(storage, storageClass));
        Resource resource = new Resource();
        resource.setLimits(Limit.builder().cpu(cpu + "").memory(memory + "Gi").build());
        resource.setRequests(Request.builder().build());
        instance.setResources(resource);
        return instance;
    }

    /**
     * 获取cr instance 节点数量
     *
     * @param databaseCluster
     * @return
     */
    @Override
    public Integer getNodeNum(DatabaseCluster databaseCluster) {
        DatabaseClusterStatus databaseClusterStatus = databaseCluster.getStatus();
        if (databaseClusterStatus == null) {
            return null;
        }
        List<StatusInstance> statusInstances = databaseClusterStatus.getInstances();
        if (null == statusInstances || statusInstances.isEmpty()) {
            return null;
        }
        StatusInstance statusInstance = statusInstances.get(0);
        return statusInstance.getReplicas();
    }

    /**
     * 获取cr instance ready节点数量
     *
     * @param databaseCluster
     * @return
     */
    @Override
    public Integer getNodeReadyNum(DatabaseCluster databaseCluster) {
        DatabaseClusterStatus databaseClusterStatus = databaseCluster.getStatus();
        if (databaseClusterStatus == null) {
            return null;
        }
        List<StatusInstance> statusInstances = databaseClusterStatus.getInstances();
        if (statusInstances.isEmpty()) {
            return null;
        }
        StatusInstance statusInstance = statusInstances.get(0);
        return statusInstance.getReadyReplicas();
    }

    @Override
    public boolean isAllPodRebuild(String clusterId, String namespace, String instanceName, String instanceId) {
        boolean result = true;
        InstanceDTO instanceDTO = instanceService.getDTO(instanceId);
        List<Pod> podList = listPods(clusterId, namespace, instanceName);

        try {
            for (Pod pod : podList) {
                String startTimeStr = pod.getStatus().getStartTime();
                Date date = CommonUtil.stringToDate(startTimeStr);
                // 将date转化成GMT+8的时间
                // date = CommonUtils.dateToGMT8(date);

                if (date.before(instanceDTO.getUpdatedAt())) {
                    result = false;
                    break;
                }
            }
        } catch (Exception e) {
            log.error("[OperatorInstanceServiceImpl.isAllPodRebuild] error:", e);
            result = false;
        }
        return result;
    }

    public List<Pod> listPods(String clusterId, String namespace, String instanceName) {
        try (KubernetesClient kubernetesClient = k8sClientConfiguration.getAdminKubernetesClientById(clusterId)) {
            Map<String, String> labelFilterMap = operatorCommonService.getLabelSelector(instanceName);
            List<Pod> items =
                    kubernetesClient.pods().inNamespace(namespace).withLabels(labelFilterMap).list().getItems();
            if (CollectionUtils.isEmpty(items)) {
                log.error("listPods get pods empty! clusterId: {} namespace: {} instanceName: {}", clusterId, namespace,
                        instanceName);
                return new ArrayList<>();
            }
            return items;
        }
    }
}

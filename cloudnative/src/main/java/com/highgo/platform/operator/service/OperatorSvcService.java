package com.highgo.platform.operator.service;

import com.highgo.platform.apiserver.model.dto.InstanceNetworkDTO;
import io.fabric8.kubernetes.client.KubernetesClient;

import java.util.List;

public interface OperatorSvcService {

    /**
     * 获取实例网络信息
     * @return
     */
    public List<InstanceNetworkDTO> getInstanceNetworkDTOList(KubernetesClient kubernetesClient, String instaceId, String namespace, String crName );
}

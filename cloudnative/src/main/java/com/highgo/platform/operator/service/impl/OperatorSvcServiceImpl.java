package com.highgo.platform.operator.service.impl;

import com.highgo.platform.apiserver.model.dto.InstanceNetworkDTO;
import com.highgo.cloud.enums.NetworkType;
import com.highgo.platform.operator.service.OperatorSvcService;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OperatorSvcServiceImpl implements OperatorSvcService {


    /**
     * 获取实例网络信息
     *
     * @param namespace
     * @param crName
     * @return
     */
    @Override
    public List<InstanceNetworkDTO> getInstanceNetworkDTOList(KubernetesClient kubernetesClient, String instanceId, String namespace, String crName) {
        List<InstanceNetworkDTO> instanceNetworkDTOList = new ArrayList<>();
        InstanceNetworkDTO instanceNetworkDTO = new InstanceNetworkDTO();
        io.fabric8.kubernetes.api.model.Service service = kubernetesClient.services().inNamespace(namespace).withName(crName+"-ha").get();
        instanceNetworkDTO.setPort(service.getSpec().getPorts().get(0).getPort());
        instanceNetworkDTO.setService(String.format("%s-ha.%s.svc.cluster.local", crName, namespace));
        instanceNetworkDTO.setType(NetworkType.RW);
        instanceNetworkDTO.setNodePort(service.getSpec().getPorts().get(0).getNodePort());
        instanceNetworkDTO.setInstanceId(instanceId);
        instanceNetworkDTOList.add(instanceNetworkDTO);
        return instanceNetworkDTOList;
    }
}

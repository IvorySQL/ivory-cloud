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
    public List<InstanceNetworkDTO> getInstanceNetworkDTOList(KubernetesClient kubernetesClient, String instanceId,
            String namespace, String crName) {
        List<InstanceNetworkDTO> instanceNetworkDTOList = new ArrayList<>();
        InstanceNetworkDTO instanceNetworkDTO = new InstanceNetworkDTO();
        io.fabric8.kubernetes.api.model.Service service =
                kubernetesClient.services().inNamespace(namespace).withName(crName + "-ha").get();
        instanceNetworkDTO.setPort(service.getSpec().getPorts().get(0).getPort());
        instanceNetworkDTO.setService(String.format("%s-ha.%s.svc.cluster.local", crName, namespace));
        instanceNetworkDTO.setType(NetworkType.RW);
        instanceNetworkDTO.setNodePort(service.getSpec().getPorts().get(0).getNodePort());
        instanceNetworkDTO.setInstanceId(instanceId);
        instanceNetworkDTOList.add(instanceNetworkDTO);
        return instanceNetworkDTOList;
    }
}

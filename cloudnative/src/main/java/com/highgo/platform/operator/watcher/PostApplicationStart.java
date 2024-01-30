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

import com.highgo.cloud.util.CommonUtil;
import com.highgo.platform.apiserver.model.vo.response.ClusterInfoVO;
import com.highgo.platform.apiserver.service.K8sClusterService;
import com.highgo.platform.operator.ElectLeader;
import io.fabric8.kubernetes.api.model.Namespace;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Component
public class PostApplicationStart implements ApplicationRunner {

    @Resource
    private K8sClusterService k8sClusterService;
    @Resource
    private ElectLeader electLeader;
    @Value("${common.namespace:ivory}")
    private String namespace;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<ClusterInfoVO> list = k8sClusterService.list();
        if (CommonUtil.isEmpty(list)) {
            return;
        }

        ClusterInfoVO clusterInfoVO = list.get(0);
        List<Namespace> namespaces = k8sClusterService.getNamespace(clusterInfoVO.getClusterId());

        if (!CommonUtil.isEmpty(namespaces)) {
            Optional<Namespace> namespaceOptional =
                    namespaces.stream().filter(item -> item.getMetadata().getName().equals(namespace)).findFirst();
            if (!namespaceOptional.isPresent()) {
                k8sClusterService.createNamespace(clusterInfoVO.getClusterId(), namespace);
            }
        }
        electLeader.initLeaderElector();
    }
}

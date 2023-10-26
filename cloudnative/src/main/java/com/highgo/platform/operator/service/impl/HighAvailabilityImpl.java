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

import com.highgo.cloud.constant.OperatorConstant;
import com.highgo.platform.apiserver.model.vo.request.ClusterVO;
import com.highgo.platform.configuration.K8sClientConfiguration;
import com.highgo.platform.operator.service.HighAvailabilityService;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Map;

@Service
public class HighAvailabilityImpl implements HighAvailabilityService {

    private static final Logger logger = LoggerFactory.getLogger(HighAvailabilityImpl.class);

    @Value("${common.serviceName}")
    private String serviceName;

    @Value("${server.port:8080}")
    private int serverPort;

    @Value("${common.request-path-prefix}")
    private String requestPathPrefix;

    @Resource
    private RestTemplate restTemplate;

    @Autowired
    private K8sClientConfiguration k8sClientConfiguration;

    /**
     * 在master节点启动指定clusterid的watcher
     *
     * @param clusterId
     * @return
     */
    @Override
    public boolean startWatcherByClusterIdOnMaster(String clusterId) {
        String podIp = getMasterIp();
        String getConfigUrl =
                String.format("http://%s:%s/%s/v1/watcher/action/start", podIp, serverPort, requestPathPrefix);
        // TODO lcq
        HttpHeaders headers = new HttpHeaders();
        // HttpHeaders headers = iamService.getCommonHeaders();
        ClusterVO clusterVO = new ClusterVO();
        clusterVO.setClusterId(clusterId);
        HttpEntity httpEntity = new HttpEntity(clusterVO, headers);
        ResponseEntity<String> responseEntity =
                restTemplate.exchange(getConfigUrl, HttpMethod.POST, httpEntity, String.class);
        int statuscode = responseEntity.getStatusCode().value();
        if (!HttpStatus.OK.equals(responseEntity.getStatusCode())) {
            logger.error(
                    "[HighAvailabilityImpl.startWatcherByClusterIdOnMaster] request master node start watcher error, status code is {}. masterip is {}, clusterid is {}",
                    statuscode, podIp, clusterId);
            return false;
        } else {
            logger.info(
                    "[HighAvailabilityImpl.startWatcherByClusterIdOnMaster] request master node start watcher success, status code is {}. masterip is {}, clusterid is {}",
                    statuscode, podIp, clusterId);
            return true;
        }
    }

    /**
     * 获取主节点的ip
     *
     * @return
     */
    @Override
    public String getMasterIp() {
        String namespace = System.getenv().get("NAMESPACE");
        if (namespace == null) {
            logger.warn("[HighAvailabilityImpl.getMasterIp] env NAMESPACE is empty. will set value cnp-system");
            namespace = "highgo";
        }
        KubernetesClient kubernetesClient = k8sClientConfiguration.getDefaultClient();
        ConfigMap configMap =
                kubernetesClient.configMaps().inNamespace(namespace).withName(serviceName + "-leader").get();
        Assert.notNull(configMap,
                String.format("[HighAvailabilityImpl.getMasterIp] can not find configMap %s in namespace %s",
                        serviceName + "-leader", namespace));
        Map<String, String> dataMap = configMap.getData();
        Assert.notNull(dataMap,
                String.format("[HighAvailabilityImpl.getMasterIp] config %s in namespace %s data is empty.",
                        serviceName + "-leader", namespace));
        String podIp = configMap.getData().get(OperatorConstant.PODIP);
        Assert.notNull(dataMap,
                String.format("[HighAvailabilityImpl.getMasterIp] config %s in namespace %s data  can not find key %s.",
                        serviceName + "-leader", namespace, OperatorConstant.PODIP));
        return podIp;
    }
}

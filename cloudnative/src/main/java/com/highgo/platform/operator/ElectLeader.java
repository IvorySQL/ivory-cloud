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

package com.highgo.platform.operator;

import com.highgo.cloud.constant.OperatorConstant;
import com.highgo.platform.configuration.K8sClientConfiguration;
import com.highgo.platform.operator.watcher.WatcherFactory;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.kubernetes.client.extended.leaderelection.LeaderElectionConfig;
import io.kubernetes.client.extended.leaderelection.LeaderElector;
import io.kubernetes.client.extended.leaderelection.resourcelock.ConfigMapLock;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.util.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Component
public class ElectLeader {

    private static final Logger logger = LoggerFactory.getLogger(ElectLeader.class);

    @Value("${common.serviceName}")
    private String serviceName;

    @Value("${common.namespace:ivory}")
    private String namespace;

    @Value("${common.leaseDuration:60000}")
    private long leaseDuration; // Leader 持有锁的时长

    @Value("${common.renewDeadline:30000}")
    private long renewDeadline; // 续约时间间隔，每隔一段时间 Leader 就需要对锁进行续约

    @Value("${common.retryPeriod:20000}")
    private long retryPeriod; // 重试时间间隔，其他 Pod 不断争抢锁的时间间隔

    @Autowired
    private WatcherFactory watcherFactory;

    @Autowired
    private K8sClientConfiguration k8sClientConfiguration;

    public boolean isLeader = false;

    /**
     * Callback used to run the bean.
     *
     * @throws Exception on error
     */
    public void initLeaderElector() throws Exception {

        ApiClient client = Config.defaultClient();
        Configuration.setDefaultApiClient(client);

        // 使用 IP 作为 Identity
        String lockHolderIdentityName = System.getenv("HOSTNAME") != null ? System.getenv("HOSTNAME")
                : InetAddress.getLocalHost().getHostAddress();
        // 创建 ConfigMap 锁
        ConfigMapLock lock = new ConfigMapLock(namespace, serviceName + "-leader", lockHolderIdentityName);
        // Leader 选举的配置
        LeaderElectionConfig leaderElectionConfig =
                new LeaderElectionConfig(lock,
                        Duration.ofMillis(leaseDuration),
                        Duration.ofMillis(renewDeadline),
                        Duration.ofMillis(retryPeriod));

        // 初始化 LeaderElector
        LeaderElector leaderElector = new LeaderElector(leaderElectionConfig);
        // 选举 Leader
        leaderElector.run(
                () -> {
                    logger.info("[ElectServiceImpl.electLeader] I get leader, {}", lockHolderIdentityName);
                    isLeader = true;
                    watcherFactory.initStart();
                    // 设置master节点podip到cm
                    KubernetesClient kubernetesClient = k8sClientConfiguration.getDefaultClient();
                    ConfigMap configMap = kubernetesClient.configMaps().inNamespace(System.getenv().get("NAMESPACE"))
                            .withName(serviceName + "-leader").get();
                    Map<String, String> dataMap = new HashMap<>();
                    dataMap.put(OperatorConstant.PODIP, System.getenv().get("PODIP"));
                    configMap.setData(dataMap);
                    kubernetesClient.configMaps().inNamespace(System.getenv().get("NAMESPACE"))
                            .withName(serviceName + "-leader").patch(configMap);
                },
                () -> {
                    logger.info("[ElectServiceImpl.electLeader] I lost leader, {}", lockHolderIdentityName);
                    watcherFactory.stopAllWatcher();
                });

    }

}

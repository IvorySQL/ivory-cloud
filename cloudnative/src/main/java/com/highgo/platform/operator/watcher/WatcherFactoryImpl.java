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

import com.highgo.platform.apiserver.service.K8sClusterService;
import com.highgo.platform.operator.ElectLeader;
import com.highgo.platform.operator.cr.DatabaseCluster;
import com.highgo.platform.operator.service.HighAvailabilityService;
import com.highgo.platform.apiserver.model.po.K8sClusterInfoPO;
import com.highgo.platform.configuration.K8sClientConfiguration;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;
import io.fabric8.kubernetes.client.informers.SharedInformerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.InetAddress;
import java.util.*;
import java.util.stream.Collectors;

/**
 * watcher工厂类
 */
public abstract class WatcherFactoryImpl implements WatcherFactory {

    private static final Logger logger = LoggerFactory.getLogger(WatcherFactoryImpl.class);
    /**
     * 已启动watcher的集群缓存
     */
    public static Map<String, SharedInformerFactory> clusterWatcherCache = new HashMap<>();

    @Autowired
    private K8sClusterService k8sClusterService;

    @Autowired
    private K8sClientConfiguration k8sClientConfiguration;

    @Autowired
    private ElectLeader electLeader;

    @Autowired
    private HighAvailabilityService highAvailabilityService;

    /**
     * 在所有集群上启动watcher
     */
    @Override
    public void initStart() {
        for (K8sClusterInfoPO k8sClusterInfoPO : k8sClusterService.getK8sClusterList()) {
            String clusterId = k8sClusterInfoPO.getClusterId();
            startWatcherById(clusterId);
        }
    }

    @Override
    public void refresh() {
        List<K8sClusterInfoPO> k8sClusterInfoPOList = k8sClusterService.getK8sClusterList();
        List<String> clusterIdListDB = k8sClusterInfoPOList
                .stream()
                .map(K8sClusterInfoPO::getClusterId)
                .collect(Collectors.toList());
        logger.info(
                "[WatcherFactoryImpl.refresh] before refresh, clusterId in clusterIdListDB: {}, clusterWatcherCache:{}",
                Arrays.toString(clusterIdListDB.toArray()), Arrays.toString(clusterWatcherCache.keySet().toArray()));
        // 从缓存中移除取消ping不通的集群

        List<String> unReachableClusters = k8sClusterInfoPOList
                .stream()
                .filter(k -> {
                    try {
                        return !InetAddress.getByName(k.getServerUrl()).isReachable(5000);
                    } catch (IOException e) {
                        return false;
                    }
                })
                .map(K8sClusterInfoPO::getClusterId)
                .peek(i -> {
                    if (clusterWatcherCache.containsKey(i)) {
                        clusterWatcherCache.get(i).stopAllRegisteredInformers();
                        clusterWatcherCache.remove(i);
                    }
                })
                .collect(Collectors.toList());
        logger.info(
                "[WatcherFactoryImpl.refresh] remove cluster from cache,remove cluster list:{},cache cluster list:{}",
                Arrays.toString(unReachableClusters.toArray()),
                Arrays.toString(clusterWatcherCache.keySet().toArray()));

        // for (Iterator<Map.Entry<String, SharedInformerFactory>> it = clusterWatcherCache.entrySet().iterator();
        // it.hasNext(); ) {
        // Map.Entry<String, SharedInformerFactory> item = it.next();
        // String clusterId = item.getKey();
        // if (!clusterIdListDB.contains(clusterId)) {
        // SharedInformerFactory sharedInformerFactory = item.getValue();
        // sharedInformerFactory.stopAllRegisteredInformers();
        // it.remove();
        // }
        // }
        // 添加新纳管的集群
        for (K8sClusterInfoPO k8sClusterInfoPO : k8sClusterInfoPOList) {
            if (!clusterWatcherCache.containsKey(k8sClusterInfoPO.getClusterId())) {
                startWatcherById(k8sClusterInfoPO.getClusterId());
            }
        }
        logger.info("[WatcherFactoryImpl.refresh] after refresh, clusterWatcherCache:{}",
                Arrays.toString(clusterWatcherCache.keySet().toArray()));

    }

    @Override
    public synchronized boolean startWatcherById(String clusterId) {
        boolean relust = true;
        try {
            if (electLeader.isLeader) {

                KubernetesClient kubernetesClient = null;
                try {
                    kubernetesClient = k8sClientConfiguration.getAdminKubernetesClientById(clusterId);
                } catch (Exception e) {
                    logger.error("[WatcherFactoryImpl.startWatcherById] The client failed to connect to k8s");
                    logger.error("[WatcherFactoryImpl.startWatcherById] Skip starting watcher of k8s {}", clusterId);
                    return false;
                }

                // 是leader 启动watcher
                logger.info(
                        "[WatcherFactoryImpl.startWatcherById] I am leader, checking cluster watcher was started? clusterId {}",
                        clusterId);
                if (!clusterWatcherCache.containsKey(clusterId)) {
                    SharedInformerFactory sharedInformerFactory = kubernetesClient.informers();
                    registerInformers(kubernetesClient, sharedInformerFactory, clusterId);
                    sharedInformerFactory.startAllRegisteredInformers();
                    clusterWatcherCache.put(clusterId, sharedInformerFactory);
                    logger.info(
                            "[WatcherFactoryImpl.startWatcherById] new cluster, start watcher success. cluster id {}",
                            clusterId);
                } else {
                    SharedInformerFactory sharedInformerFactory = kubernetesClient.informers();
                    SharedIndexInformer<DatabaseCluster> existingSharedIndexInformer =
                            sharedInformerFactory.getExistingSharedIndexInformer(DatabaseCluster.class);
                    if (!existingSharedIndexInformer.isRunning() || !existingSharedIndexInformer.isWatching()) {
                        logger.info(
                                "[WatcherFactoryImpl.startWatcherById] watcher already started, but not working, now reboot. cluster id {}",
                                clusterId);
                        registerInformers(kubernetesClient, sharedInformerFactory, clusterId);
                        sharedInformerFactory.startAllRegisteredInformers();
                        clusterWatcherCache.put(clusterId, sharedInformerFactory);
                    } else {
                        logger.info(
                                "[WatcherFactoryImpl.startWatcherById] watcher already started, no need start agin. cluster id {}",
                                clusterId);
                    }
                }
            } else {
                // 不是leader，转发到leader节点启动watcher
                logger.info(
                        "[WatcherFactoryImpl.startWatcherById] I am not leader, rerequest to leader node to start watcher? clusterId {}",
                        clusterId);
                relust = highAvailabilityService.startWatcherByClusterIdOnMaster(clusterId);
            }
        } catch (Exception e) {
            logger.error("[WatcherFactoryImpl.startWatcherById] watcher factory start watcher failed. cluster id: {}",
                    clusterId, e);
            relust = false;
        }
        return relust;
    }

    @Override
    public synchronized boolean stopWatcherById(String clusterId) {
        if (!clusterWatcherCache.containsKey(clusterId)) {
            logger.warn("[WatcherFactoryImpl.stopWatcherById] watcher is not exist on cluster {}, no need to stop it.",
                    clusterId);
            return true;
        }
        SharedInformerFactory sharedInformerFactory = clusterWatcherCache.get(clusterId);
        sharedInformerFactory.stopAllRegisteredInformers();
        clusterWatcherCache.remove(clusterId);
        logger.info("[WatcherFactoryImpl.stopWatcherById] watcher stop success on cluster {}", clusterId);
        return true;
    }

    @Override
    public void stopAllWatcher() {
        for (Iterator<Map.Entry<String, SharedInformerFactory>> it = clusterWatcherCache.entrySet().iterator(); it
                .hasNext();) {
            Map.Entry<String, SharedInformerFactory> item = it.next();
            SharedInformerFactory sharedInformerFactory = item.getValue();
            sharedInformerFactory.stopAllRegisteredInformers();
            it.remove();
        }
        logger.info("[WatcherFactoryImpl.stopAllWatcher] all watcher stoped!");
    }

    /**
     * 注册watcher
     * e.g
     * SharedIndexInformer<PostgresCluster> postgresClusterSharedIndexInformer = sharedInformerFactory.sharedIndexInformerForCustomResource(PostgresCluster.class, 30 * 1000L);
     * PostgresClusterWatcher postgresClusterWatcher = new PostgresClusterWatcher();
     * postgresClusterSharedIndexInformer.addEventHandler(postgresClusterWatcher.initResourceEventHandler());
     *
     * @param sharedInformerFactory
     */
    public abstract void registerInformers(KubernetesClient kubernetesClient,
            SharedInformerFactory sharedInformerFactory, String clusterId);
}

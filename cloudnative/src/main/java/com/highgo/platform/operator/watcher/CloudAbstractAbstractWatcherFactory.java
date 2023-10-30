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

import com.highgo.platform.operator.cr.DatabaseCluster;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;
import io.fabric8.kubernetes.client.informers.SharedInformerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * watcher工厂类
 */
@Component
public class CloudAbstractAbstractWatcherFactory extends AbstractWatcherFactory {

    @Resource
    private CloudClusterWatcher cloudClusterWatcher;

    @Override
    public void registerInformers(KubernetesClient kubernetesClient, SharedInformerFactory sharedInformerFactory,
            String clusterId) {
        SharedIndexInformer<DatabaseCluster> clusterSharedIndexInformer =
                sharedInformerFactory.sharedIndexInformerForCustomResource(
                        DatabaseCluster.class, 30 * 1000L);
        cloudClusterWatcher.setClusterId(clusterId);
        cloudClusterWatcher.setKubernetesClient(kubernetesClient);

        clusterSharedIndexInformer.addEventHandler(cloudClusterWatcher.initResourceEventHandler());
    }

}

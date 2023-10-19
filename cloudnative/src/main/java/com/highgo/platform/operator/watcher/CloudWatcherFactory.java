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

import com.highgo.platform.apiserver.service.AlertAutoScalingService;
import com.highgo.platform.apiserver.service.ExtraMetaService;
import com.highgo.platform.apiserver.service.InstanceService;
import com.highgo.platform.apiserver.service.RestoreRecordService;
import com.highgo.platform.operator.cr.DatabaseCluster;
import com.highgo.platform.operator.service.*;
import com.highgo.platform.operator.service.impl.OperatorBackupServiceImpl;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;
import io.fabric8.kubernetes.client.informers.SharedInformerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * watcher工厂类
 */
@Component
public class CloudWatcherFactory extends WatcherFactoryImpl {

    @Resource
    private InstanceService instanceService;

    @Resource
    private OperatorInstanceService operatorInstanceService;

    @Resource
    private CrService crService;

    @Resource
    private OperatorCommonService operatorCommonService;

    @Resource
    private OperatorSvcService operatorSvcService;

    @Autowired
    private OperatorUserService operatorUserService;

    @Autowired
    private ExtraMetaService extraMetaService;

    @Autowired
    private OperatorBackupServiceImpl operatorBackupsService;

    @Resource
    private OperatorRestoreService operatorRestoreService;

    @Resource
    private AlertAutoScalingService alertAutoScalingService;

    @Resource
    private RestoreRecordService restoreRecordService;

    @Resource
    private CloudClusterWatcher cloudClusterWatcher;

    @Override
    public void registerInformers(KubernetesClient kubernetesClient, SharedInformerFactory sharedInformerFactory,
            String clusterId) {
        SharedIndexInformer<DatabaseCluster> clusterSharedIndexInformer =
                sharedInformerFactory.sharedIndexInformerForCustomResource(
                        DatabaseCluster.class, 30 * 1000L);
        cloudClusterWatcher.setClusterId(clusterId);
        cloudClusterWatcher.setCrService(crService);
        cloudClusterWatcher.setAlertAutoScalingService(alertAutoScalingService);
        cloudClusterWatcher.setInstanceService(instanceService);
        cloudClusterWatcher.setExtraMetaService(extraMetaService);
        cloudClusterWatcher.setKubernetesClient(kubernetesClient);
        cloudClusterWatcher.setOperatorBackupsService(operatorBackupsService);
        cloudClusterWatcher.setOperatorCommonService(operatorCommonService);
        cloudClusterWatcher.setOperatorSvcService(operatorSvcService);
        cloudClusterWatcher.setOperatorRestoreService(operatorRestoreService);
        cloudClusterWatcher.setOperatorInstanceService(operatorInstanceService);
        cloudClusterWatcher.setOperatorUserService(operatorUserService);
        cloudClusterWatcher.setRestoreRecordService(restoreRecordService);

        clusterSharedIndexInformer.addEventHandler(cloudClusterWatcher.initResourceEventHandler());
    }

}

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
    public void registerInformers(KubernetesClient kubernetesClient, SharedInformerFactory sharedInformerFactory, String clusterId) {
        SharedIndexInformer<DatabaseCluster> clusterSharedIndexInformer = sharedInformerFactory.sharedIndexInformerForCustomResource(
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

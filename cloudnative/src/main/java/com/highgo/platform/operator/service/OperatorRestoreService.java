package com.highgo.platform.operator.service;


import com.highgo.platform.apiserver.model.dto.InstanceDTO;
import com.highgo.platform.apiserver.model.dto.RestoreRecordDTO;
import com.highgo.platform.operator.cr.bean.backup.Restore;
import com.highgo.platform.operator.cr.bean.backup.RestoreDatasource;
import com.highgo.platform.operator.cr.bean.status.RestoreStatus;
import io.fabric8.kubernetes.client.KubernetesClient;

/**
 * @author lucunqiao
 * @date 2023/1/18
 */
public interface OperatorRestoreService {


    RestoreDatasource getDataSourceCluster(InstanceDTO instanceDTO);

    void restoreCallback(KubernetesClient kubernetesClient, String instanceId, RestoreStatus restore, RestoreRecordDTO restoreRecord);

    Restore genPgbackuprestRestore(InstanceDTO instanceDTO);
}

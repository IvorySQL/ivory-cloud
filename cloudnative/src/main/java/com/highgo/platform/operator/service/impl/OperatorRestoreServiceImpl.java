package com.highgo.platform.operator.service.impl;

import com.highgo.platform.apiserver.model.dto.BackupDTO;
import com.highgo.platform.apiserver.model.dto.InstanceDTO;
import com.highgo.platform.apiserver.model.dto.RestoreRecordDTO;
import com.highgo.platform.apiserver.service.BackupService;
import com.highgo.platform.apiserver.service.InstanceService;
import com.highgo.platform.apiserver.service.RestoreRecordService;
import com.highgo.platform.apiserver.service.RestoreService;
import com.highgo.platform.errorcode.BackupError;
import com.highgo.platform.errorcode.RestoreError;
import com.highgo.platform.exception.BackupException;
import com.highgo.platform.exception.RestoreException;
import com.highgo.platform.operator.cr.DatabaseCluster;
import com.highgo.platform.operator.cr.bean.backup.Restore;
import com.highgo.platform.operator.cr.bean.backup.RestoreDatasource;
import com.highgo.platform.operator.cr.bean.backup.RestoreCluster;
import com.highgo.platform.operator.cr.bean.status.RestoreStatus;
import com.highgo.platform.operator.service.OperatorRestoreService;
import io.fabric8.kubernetes.client.KubernetesClient;
import com.highgo.cloud.util.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author lucunqiao
 * @date 2023/1/18
 */
@Service
public class OperatorRestoreServiceImpl implements OperatorRestoreService {
    private static final Logger logger = LoggerFactory.getLogger(OperatorRestoreServiceImpl.class);

    @Autowired
    private InstanceService instanceService;

    @Autowired
    private BackupService backupService;

    @Autowired
    private RestoreService restoreService;

    @Resource
    private RestoreRecordService restoreRecordService;

    @Override
    public RestoreDatasource getDataSourceCluster(InstanceDTO instanceDTO) {
        InstanceDTO originInstanceDTO = instanceService.getDTO(instanceDTO.getOriginalInstanceId());

        BackupDTO backup = backupService.getBackupByBackupId(instanceDTO.getOriginalBackupId());
        if (backup == null) {
            throw new BackupException(BackupError.BACKUP_NOT_EXIST);
        }

        try {
            List<String> options = new ArrayList(Arrays.asList("--type=time", String.format("--target=\"%s\"", CommonUtil.dateToStr(backup.getCreatedAt()))));
            return RestoreDatasource
                    .builder()
                    .postgresCluster(RestoreCluster
                            .builder()
                            .clusterName(originInstanceDTO.getName())
                            .clusterNamespace(instanceDTO.getNamespace())
                            .options(options)
                            .build())
                    .build();
        } catch (ParseException e) {
            logger.error("[OperatorRestoreServiceImpl.getRestoreTime] error ", e);
            throw new RestoreException(RestoreError.RESTORE_TIME_ERROR);
        }
    }

    @Override
    public void restoreCallback(KubernetesClient kubernetesClient, String instanceId, RestoreStatus restoreStatus, RestoreRecordDTO restoreRecord) {

        String restoreId = restoreStatus.getId();
        String backupId = restoreId.split(":")[0];

        InstanceDTO dto = instanceService.getDTO(instanceId);

        //修改cr  restore为false
        io.fabric8.kubernetes.client.dsl.Resource<DatabaseCluster> clusterResource = kubernetesClient.customResources(DatabaseCluster.class).inNamespace(dto.getNamespace()).withName(dto.getName());
        DatabaseCluster databaseCluster = clusterResource.get();
        databaseCluster.getSpec().getBackups().getPgbackrest().getRestore().setEnabled(false);
        clusterResource.patch(databaseCluster);
        if (restoreRecord == null) {
            restoreRecord = new RestoreRecordDTO();
        }
        restoreRecord.setFinished(restoreStatus.getFinished());
        restoreRecord.setInstanceId(instanceId);
        restoreRecord.setStartTime(restoreStatus.getStartTime());
        restoreRecord.setCompletionTime(restoreStatus.getCompletionTime());
        
        restoreRecordService.createOrModifyRestoreRecord(restoreRecord);
        restoreService.restoreInstanceCallBack(instanceId, backupId, restoreStatus.getSucceeded() !=null && restoreStatus.getSucceeded() > 0);
    }

    @Override
    public Restore genPgbackuprestRestore(InstanceDTO instanceDTO) {
        try {
            BackupDTO backup = backupService.getBackupByBackupId(instanceDTO.getOriginalBackupId());
            if (backup == null) {
                throw new BackupException(BackupError.BACKUP_NOT_EXIST);
            }
            Restore restore = new Restore();
            //restore.setAffinity(operatorCommonService.getAffinity(instanceDTO.getName()));
            restore.setEnabled(true);
            restore.getOptions().add(String.format("--target=\"%s\"", CommonUtil.dateToStr(backup.getCreatedAt())));
            return restore;
        } catch (ParseException e) {
            logger.error("[OperatorRestoreServiceImpl.getRestoreTime] error ", e);
            throw new RestoreException(RestoreError.RESTORE_TIME_ERROR);
        }
    }
}

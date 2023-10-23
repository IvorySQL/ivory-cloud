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

import com.highgo.cloud.enums.BackupStatus;
import com.highgo.platform.apiserver.model.dto.BackupDTO;
import com.highgo.platform.apiserver.service.BackupService;
import com.highgo.platform.operator.cr.bean.backup.*;
import com.highgo.platform.operator.cr.bean.common.StorageRequests;
import com.highgo.platform.operator.cr.bean.common.StorageResource;
import com.highgo.platform.operator.cr.bean.common.VolumeClaimSpec;
import com.highgo.platform.operator.cr.bean.status.ManualBackupStatus;
import com.highgo.platform.operator.service.OperatorBackupsService;
import io.fabric8.kubernetes.api.model.batch.v1.JobList;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class OperatorBackupServiceImpl implements OperatorBackupsService {

    @Value(value = "${images.image.backrest}")
    private String backrestImage;

    @Autowired
    private BackupService backupService;

    @Value("${cluster.clusterNameLabel}")
    private String clusterNameLabel;

    @Value("${cluster.crBackupAnnotation}")
    private String crBackupAnnotation;
    /**
     * 构建备份对象
     *
     * @param storage
     * @param storageClass
     * @return
     */
    @Override
    public Backup getBackupLocal(String storage, String storageClass) {
        Backup backup = new Backup();
        PgBackrest pgbackrest = new PgBackrest();
        pgbackrest.setImage(backrestImage);
        Manual manual = new Manual();
        pgbackrest.setManual(manual);
        Repo repoLocal = getRepoLocal(storage, storageClass);
        pgbackrest.setRepos(new ArrayList<>(Arrays.asList(repoLocal)));
        // 去掉保留天数设置
        // Map<String,String> globalMap = new HashMap<>();
        // globalMap.put("repo1-retention-full", "14");
        // globalMap.put("repo1-retention-full-type", "time");
        // highgoDBbackrest.setGlobal(globalMap);
        backup.setPgbackrest(pgbackrest);
        return backup;
    }

    /**
     * 获取s3备份仓库对象
     * @param bucket
     * @param endpoint
     * @param region
     * @return
     */
    @Override
    public Backup getBackupS3(String bucket, String endpoint, String region, String s3SecretName, String instanceName) {
        Backup backup = new Backup();
        PgBackrest pgbackrest = new PgBackrest();
        // 镜像
        pgbackrest.setImage(backrestImage);
        // s3secret
        Map<String, String> configurationMap = new HashMap<>();
        configurationMap.put("secret", s3SecretName);
        pgbackrest.setConfiguration(new ArrayList<>(Arrays.asList(configurationMap)));
        // s3连接信息
        Repo repoS3 = geRepoS3(bucket, endpoint, region);
        pgbackrest.setRepos(new ArrayList<>(Arrays.asList(repoS3)));
        // global 仓库名称配置
        Map<String, String> globalMap = new HashMap<>();
        globalMap.put(repoS3.getName() + "-path", "/pgbackupset/" + instanceName);
        // globalMap.put("repo1-retention-full", "14");
        // globalMap.put("repo1-retention-full-type", "time");
        pgbackrest.setGlobal(globalMap);
        backup.setPgbackrest(pgbackrest);
        return backup;
    }

    @Override
    public Repo getRepoLocal(String storage, String storageClass) {
        Repo repo = new Repo();
        Volume volume = new Volume();
        VolumeClaimSpec volumeClaimSpec = new VolumeClaimSpec();
        volumeClaimSpec.setResources(
                StorageResource.builder().requests(StorageRequests.builder().storage(storage).build()).build());
        volumeClaimSpec.setStorageClassName(storageClass);
        volume.setVolumeClaimSpec(volumeClaimSpec);
        repo.setVolume(volume);
        return repo;
    }

    /**
     * 构建s3备份对象
     *
     * @param bucket
     * @param endpoint
     * @param region
     * @return
     */
    @Override
    public Repo geRepoS3(String bucket, String endpoint, String region) {
        Repo repoS3 = new Repo();
        repoS3.setS3(S3.builder().bucket(bucket).endpoint(endpoint).region(region).build());
        return repoS3;
    }

    @Override
    public boolean isBackupInit(KubernetesClient kubernetesClient, String namespace, String instanceName) {
        Map<String, String> labelMap = new HashMap<>();
        labelMap.put(clusterNameLabel, instanceName);
        labelMap.put(crBackupAnnotation, "replica-create");
        JobList jobList = kubernetesClient.batch().jobs().inNamespace(namespace).withLabels(labelMap).list();
        if (jobList.getItems().size() > 0 && jobList.getItems().get(0).getStatus().getSucceeded() != null
                && jobList.getItems().get(0).getStatus().getSucceeded() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 同步cr手动备份记录状态到数据库
     */
    @Override
    public void syncManualBackup(ManualBackupStatus newManualBackupStatus) {
        // 手动备份事件
        String backupId = newManualBackupStatus.getId();
        BackupDTO backupDTO = backupService.getBackupByBackupId(backupId);
        if (null == backupDTO) {
            // todo 从命令行触发手动备份，表中不存在备份记录，插入备份记录
            return;

        }
        if (!BackupStatus.PROCESSING.equals(backupDTO.getStatus())) {
            // 库里状态已更新，无需再处理
            return;
        }

        log.info("ManualBackupStatus : {}, {} ,{}, {}, {}", newManualBackupStatus.getId(),
                newManualBackupStatus.getFinished(), newManualBackupStatus.getSucceeded(),
                newManualBackupStatus.getFailed(), newManualBackupStatus);
        if (newManualBackupStatus.getFinished()) {
            if (newManualBackupStatus.getSucceeded() != null && newManualBackupStatus.getSucceeded() > 0) {
                backupService.createBackupCallback(backupId, true);
            } else {
                backupService.createBackupCallback(backupId, false);
            }
        }
    }

}

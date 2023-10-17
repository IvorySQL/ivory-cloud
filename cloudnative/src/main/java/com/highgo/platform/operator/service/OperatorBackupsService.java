package com.highgo.platform.operator.service;


import com.highgo.platform.operator.cr.bean.backup.Backup;
import com.highgo.platform.operator.cr.bean.backup.Repo;
import com.highgo.platform.operator.cr.bean.status.ManualBackupStatus;
import io.fabric8.kubernetes.client.KubernetesClient;

public interface OperatorBackupsService {


    /**
     *构建备份对象
     * @return
     */
    public Backup getBackupLocal(String storage, String storageClass);

    /**
     *构建备份对象
     * @return
     */
    public Backup getBackupS3(String bucket, String endpoint, String region, String s3SecretName, String instanceName);

    /**
     * 构建仓库对象 本地存储
     * @return
     */
    public Repo getRepoLocal(String storage, String storageClass);

    /**
     * 构建s3备份对象
     * @param
     * @return
     */
    public Repo geRepoS3(String bucket, String endpoint, String region);

    boolean isBackupInit(KubernetesClient kubernetesClient, String namespace, String name);

    /**
     * 同步cr手动备份记录状态到数据库
     */
    public void syncManualBackup(ManualBackupStatus newManualBackupStatus);
}

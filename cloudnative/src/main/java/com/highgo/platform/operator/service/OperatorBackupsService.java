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
    public void syncManualBackup(String instanceId, ManualBackupStatus newManualBackupStatus);
}

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

package com.highgo.platform.apiserver.repository;

import com.highgo.platform.apiserver.model.po.BackupPO;
import com.highgo.cloud.enums.BackupStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface BackupRepository extends BaseRepository<BackupPO, String> {

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update BackupPO b set b.isDeleted = true, b.deletedAt = ?2 where b.instanceId = ?1")
    void deleteByInstanceId(String id, Date date);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update BackupPO b set b.fileName = ?3, b.updatedAt = ?2 where b.id = ?1")
    void updateBackupFile(String id, Date date, String fileName);

    @Query(value = "select b from BackupPO b where b.isDeleted = false and b.instanceId = ?1")
    List<BackupPO> listByInstanceId(String instanceId);

    @Query(value = "select b from BackupPO b where b.isDeleted = false and b.instanceId = ?1 and b.createdAt = ?2")
    Optional<BackupPO> getBackupPOByCreatedAt(String instanceId, Date date);

    @Query(value = "select b from BackupPO b where b.isDeleted = false and b.instanceId = ?1 and b.name = ?2")
    Optional<BackupPO> getBackupByName(String instanceId, String name);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update BackupPO b set b.status = ?3, b.updatedAt = ?2 where b.id = ?1")
    void updateStatus(String id, Date date, BackupStatus backupStatus);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update BackupPO b set b.isRestoring = ?3, b.updatedAt = ?2 where b.id = ?1")
    void updateIsRestoring(String id, Date date, Boolean isRestoring);
}

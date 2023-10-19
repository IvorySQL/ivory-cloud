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

import com.highgo.platform.apiserver.model.po.BackupPolicyPO;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Repository
public interface BackupPolicyRepository extends BaseRepository<BackupPolicyPO, String> {

    @Query(value = "select b from BackupPolicyPO b where b.instanceId = ?1")
    Optional<BackupPolicyPO> findByInstanceId(String instanceId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update BackupPolicyPO b  set b.isDeleted = true, b.deletedAt = ?2 where b.instanceId = ?1 ")
    void deleteByInstanceId(String instanceId, Date date);
}

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

import com.highgo.platform.apiserver.model.po.InstanceNetworkPO;
import com.highgo.cloud.enums.NetworkType;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface InstanceNetworkRepository extends BaseRepository<InstanceNetworkPO, String> {

    @Query(value = "select i from InstanceNetworkPO i where i.isDeleted = false and i.instanceId = ?1")
    List<InstanceNetworkPO> listByInstanceId(String instanceId);

    @Query(value = "select i from InstanceNetworkPO i where i.isDeleted = false and i.instanceId = ?1 and i.type = ?2")
    Optional<InstanceNetworkPO> findByIdAndType(String instanceId, NetworkType type);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update InstanceNetworkPO i set i.isDeleted = true, i.deletedAt = ?2 where i.instanceId = ?1")
    void deleteByInstanceId(String instanceId, Date date);

    @Query(value = "select i from InstanceNetworkPO i where i.isDeleted = false")
    List<InstanceNetworkPO> list();

}

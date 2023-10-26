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

import com.highgo.platform.apiserver.model.po.InstanceEventPO;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Repository
public interface InstanceEventRepository extends BaseRepository<InstanceEventPO, String> {

    @Query(value = "select i from InstanceEventPO i where i.instanceId = ?1 and i.isDeleted = false")
    Optional<InstanceEventPO> findByInstanceId(String instanceId);

    @Query(value = "select i.resourceVersion from InstanceEventPO i where i.instanceId = ?1 and i.isDeleted = false")
    Long getResourceVersion(String instanceId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update InstanceEventPO i set i.resourceVersion = ?2, i.updatedAt = ?3  where i.instanceId = ?1")
    void updateResourceVersionByInstanceId(String instanceId, long resourceVersion, Date date);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update InstanceEventPO i set i.isDeleted = true, i.deletedAt = ?2 where i.instanceId = ?1")
    void deleteByInstanceId(String instanceId, Date date);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update InstanceEventPO i set i.nodeNum = ?2, i.updatedAt = ?3 where i.instanceId = ?1")
    void updateNodeNumByInstanceId(String instanceId, int nodeNum, Date date);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update InstanceEventPO i set i.nodeReadyNum = ?2, i.updatedAt = ?3 where i.instanceId = ?1")
    void updateNodeReadyNumByInstanceId(String instanceId, int nodeReadyNum, Date date);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update InstanceEventPO i set i.stsevent = ?2, i.updatedAt = ?3 where i.instanceId = ?1")
    void updateStsEventByInstanceId(String instanceId, String stsEvent, Date date);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update InstanceEventPO i set i.podevent = ?2, i.updatedAt = ?3 where i.instanceId = ?1")
    void updatePodEventByInstanceId(String instanceId, String podEvent, Date date);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update InstanceEventPO i set i.nodeReadyNum = ?2, i.stsevent = ?3, i.podevent = ?4, i.updatedAt = ?5 where i.instanceId = ?1")
    void updateEventByInstanceId(String instanceId, int readyNum, String stsEvent, String podEvent, Date date);
}

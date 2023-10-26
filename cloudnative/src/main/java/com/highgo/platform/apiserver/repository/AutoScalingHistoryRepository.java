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

import com.highgo.platform.apiserver.model.po.AutoScalingHistoryPO;
import com.highgo.cloud.enums.AutoScalingOperation;
import com.highgo.cloud.enums.AutoScalingStatus;
import com.highgo.cloud.enums.AutoScalingType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AutoScalingHistoryRepository extends BaseRepository<AutoScalingHistoryPO, String> {

    @Override
    @Query("select a from AutoScalingHistoryPO a where a.isDeleted = false and a.id = ?1")
    Optional<AutoScalingHistoryPO> findById(String autoScalingHistoryId);

    List<AutoScalingHistoryPO> findByInstanceIdAndStatusOrderByCreatedAtDesc(String instanceId,
            AutoScalingStatus status);
    @Query("select a from AutoScalingHistoryPO a where a.isDeleted = false and a.instanceId = ?1 and a.type = ?2 and a.operation = ?3 order by a.createdAt desc")
    List<AutoScalingHistoryPO> findByInstanceIdAndSameAutoScaling(String instanceId, AutoScalingType type,
            AutoScalingOperation operation, Pageable pageable);

}

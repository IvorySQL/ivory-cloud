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

import com.highgo.platform.apiserver.model.po.AutoScalingAlertRulePO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AutoScalingAlertRuleRepository extends BaseRepository<AutoScalingAlertRulePO, String> {

    @Override
    @Query("select a from AutoScalingAlertRulePO a where a.isDeleted = false and a.id = ?1")
    Optional<AutoScalingAlertRulePO> findById(String autoScalingAlertRuleId);

    List<AutoScalingAlertRulePO> findByClusterIdAndUserIdAndIsDeleted(String cluserId, String userId,
            Boolean isDeleted);

}

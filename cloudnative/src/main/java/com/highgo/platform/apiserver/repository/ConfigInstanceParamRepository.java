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

import com.highgo.platform.apiserver.model.po.ConfigInstanceParamPO;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
public interface ConfigInstanceParamRepository extends BaseRepository<ConfigInstanceParamPO, String> {

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query("update ConfigInstanceParamPO c set c.isDeleted = true, c.deletedAt=?2 where c.instanceId = ?1")
    void deleteByInstanceId(String id, Date date);

    @Query("select c from ConfigInstanceParamPO c where c.isDeleted = false and c.instanceId = ?1")
    List<ConfigInstanceParamPO> listByInstanceId(String instanceId);
}

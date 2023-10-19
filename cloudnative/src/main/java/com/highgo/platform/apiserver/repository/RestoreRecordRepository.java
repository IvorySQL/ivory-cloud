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

import com.highgo.platform.apiserver.model.po.RestoreRecordPO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author srk
 * @version 1.0
 * @project micro_service_cloud
 * @description 数据库恢复记录表操作类
 * @date 2023/9/25 17:33:09
 */
@Repository
public interface RestoreRecordRepository extends BaseRepository<RestoreRecordPO, String> {

    /**
     * @description 根据实例id查询数据库恢复记录
     *
     * @param: instanceId
     * @return Optional<RestoreRecordPO>
     * @author srk
     * @date 2023/9/25 17:36
     */

    @Query("select i from RestoreRecordPO i where i.isDeleted = false and i.instanceId = ?1")
    Optional<RestoreRecordPO> findRestoreRecordPOByInstanceId(String instanceId);
}

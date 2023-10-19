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

import com.highgo.platform.apiserver.model.po.ConfigParamDefinationPO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ConfigDefinationRepository extends BaseRepository<ConfigParamDefinationPO, String> {

    @Query(value = "select p.name as name, p.paramType as paramType, p.defaultValue as defaultValue, c.value as runningValue, p.min as min, p.max as max, p.enumValue as enumValue, p.rule as rule, p.description as description from ConfigParamDefinationPO p LEFT JOIN ConfigInstanceParamPO c on p.name = c.name where c.instanceId = ?1 and c.isDeleted = false and p.isDeleted = false ORDER BY p.name ASC")
    List<Map> listParamByInstanceId(String instanceId);
}

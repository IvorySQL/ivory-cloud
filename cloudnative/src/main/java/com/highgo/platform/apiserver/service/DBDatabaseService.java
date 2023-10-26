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

package com.highgo.platform.apiserver.service;

import com.highgo.platform.apiserver.model.vo.request.DatabaseVO;
import com.highgo.platform.apiserver.model.vo.response.ActionResponse;

import java.util.List;

/**
 * @author lucunqiao
 * @date 2022/12/14
 */
public interface DBDatabaseService {

    /**
     * 创建数据库
     *
     * @param instanceId
     * @param databaseVO
     * @return
     */
    ActionResponse createDatabase(String instanceId, DatabaseVO databaseVO);

    /**
     * 删除数据库
     *
     * @param instanceId
     * @param dbname
     * @return
     */
    ActionResponse deleteDatabase(String instanceId, String dbname);

    /**
     * 数据库列表
     *
     * @param instanceId
     * @return
     */
    List<DatabaseVO> listDatabases(String instanceId);

}

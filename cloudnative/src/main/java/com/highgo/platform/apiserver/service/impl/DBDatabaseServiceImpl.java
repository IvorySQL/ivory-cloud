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

package com.highgo.platform.apiserver.service.impl;

import com.highgo.platform.apiserver.model.vo.request.DatabaseVO;
import com.highgo.platform.apiserver.model.dto.InstanceDTO;
import com.highgo.platform.apiserver.model.vo.response.ActionResponse;
import com.highgo.platform.apiserver.service.DBDatabaseService;
import com.highgo.platform.apiserver.service.InstanceService;
import com.highgo.platform.operator.service.OperatorUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lucunqiao
 * @date 2022/12/14
 */
@Service
public class DBDatabaseServiceImpl implements DBDatabaseService {

    private static final Logger logger = LoggerFactory.getLogger(DBDatabaseServiceImpl.class);

    @Autowired
    private InstanceService instanceService;

    @Autowired
    private OperatorUserService operatorUserService;

    @Override
    public ActionResponse createDatabase(String instanceId, DatabaseVO databaseVO) {

        InstanceDTO instanceDTO = instanceService.beforeOperateInstance(instanceId);
        operatorUserService.createDatabase(instanceDTO.getClusterId(), instanceDTO.getNamespace(),
                instanceDTO.getName(), databaseVO);
        return ActionResponse.actionSuccess();
    }

    @Override
    public ActionResponse deleteDatabase(String instanceId, String dbname) {
        return null;
    }

    @Override
    public List<DatabaseVO> listDatabases(String instanceId) {
        return null;
    }
}

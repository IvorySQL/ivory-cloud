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

package com.highgo.platform.apiserver.controller;

import com.highgo.platform.apiserver.model.vo.request.DatabaseVO;
import com.highgo.platform.apiserver.model.vo.response.ActionResponse;
import com.highgo.platform.apiserver.service.DBDatabaseService;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author lucunqiao
 * @date 2022/12/14
 */
@Validated
@RestController
@RequestMapping("${common.request-path-prefix}/${common.version}")
public class DBDatabaseController {

    @Resource
    private DBDatabaseService DBDatabaseService;

    @ApiOperation(value = "新建数据库", notes = "")
    @RequestMapping(value = "/instances/{id}/dbs", method = RequestMethod.POST)
    public ActionResponse createDatabase(@PathVariable String id, @RequestBody @Validated DatabaseVO databaseVO) {
        return DBDatabaseService.createDatabase(id, databaseVO);
    }

    @ApiOperation(value = "删除数据库", notes = "")
    @RequestMapping(value = "/instances/{id}/dbs/{dbName}", method = RequestMethod.DELETE)
    public ActionResponse deleteDatabase(@PathVariable String id, @PathVariable String dbName) {
        return DBDatabaseService.deleteDatabase(id, dbName);
    }

    @ApiOperation(value = "数据库列表", notes = "")
    @RequestMapping(value = "/instances/{id}/dbs", method = RequestMethod.GET)
    public List<DatabaseVO> listDatabase(@PathVariable String id) {
        return DBDatabaseService.listDatabases(id);
    }

}

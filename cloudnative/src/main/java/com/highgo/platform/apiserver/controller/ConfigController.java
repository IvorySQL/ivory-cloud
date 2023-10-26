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

import com.highgo.cloud.model.PageInfo;
import com.highgo.platform.apiserver.model.vo.request.ModifyConfigChangeVO;
import com.highgo.platform.apiserver.model.vo.response.ActionResponse;
import com.highgo.platform.apiserver.model.vo.response.ConfigChangeHistoryVO;
import com.highgo.platform.apiserver.model.vo.response.ConfigChangeVO;
import com.highgo.platform.apiserver.model.vo.response.ConfigParamInfoVO;
import com.highgo.platform.apiserver.service.impl.ConfigServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author lucunqiao
 * @date 2023/1/11
 */
@Validated
@RestController
@RequestMapping("${common.request-path-prefix}/${common.version}")
@Api(value = "数据库参数管理", tags = {"数据库参数管理接口"})
public class ConfigController {

    private static final Logger logger = LoggerFactory.getLogger(ConfigController.class);

    @Autowired
    private ConfigServiceImpl configService;

    @ApiOperation(value = "参数配置列表", notes = "", tags = {"OpenAPI"})
    @RequestMapping(value = "/instances/{id}/parameters", method = RequestMethod.GET)
    public List<ConfigParamInfoVO> listParamters(@Validated @PathVariable String id) {
        return configService.listParamters(id);
    }

    @ApiOperation(value = "修改参数", notes = "", tags = {"OpenAPI"})
    @RequestMapping(value = "/instances/{id}/parameters", method = RequestMethod.PUT)
    public ActionResponse modifyParameters(@Validated @PathVariable String id,
            @RequestBody ModifyConfigChangeVO modifyConfigChangeParam) {
        logger.info("[ConfigController.modifyParameters] instanceId is {}, modifyConfigChangeParam is {}", id,
                modifyConfigChangeParam.toString());
        return configService.modifyParameters(id, modifyConfigChangeParam);
    }

    @ApiOperation(value = "参数修改历史分页", notes = "", tags = {"OpenAPI"})
    @RequestMapping(value = "/instances/{id}/parameters/history/{pageNo}/{pageSize}", method = RequestMethod.GET)
    public PageInfo<List<ConfigChangeHistoryVO>> listInstance(@Validated @PathVariable String id,
            @PathVariable int pageNo, @PathVariable int pageSize) {
        return configService.listHistory(id, pageNo, pageSize);
    }

    @ApiOperation(value = "指定修改历史的参数变更列表", notes = "", tags = {"OpenAPI"})
    @RequestMapping(value = "/instances/{instanceId}/parameters/history/{configChangeHistoryId}", method = RequestMethod.GET)
    public List<ConfigChangeVO> listConfigChangeByHistory(@Validated @PathVariable String instanceId,
            @PathVariable String configChangeHistoryId) {
        return configService.listConfigChangeByHistory(instanceId, configChangeHistoryId);
    }
}

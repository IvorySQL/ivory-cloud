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

import com.highgo.cloud.auth.model.vo.UserVO;
import com.highgo.platform.apiserver.model.vo.request.CreateMonitorVO;
import com.highgo.platform.apiserver.model.vo.response.ActionResponse;
import com.highgo.platform.apiserver.service.MonitorService;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author lucunqiao
 * @date 2023/2/14
 */
@Validated
@RestController
@RequestMapping("${common.request-path-prefix}/${common.version}")
public class MonitorController {

    @Resource
    private MonitorService monitorService;

    @ApiOperation(value = "获取用户监控信息", notes = "", tags = {"OpenAPI"})
    @RequestMapping(value = "/monitor", method = RequestMethod.POST)
    public UserVO getMonitor(@RequestBody @Validated CreateMonitorVO createMonitorVO) {
        return monitorService.monitor(createMonitorVO);
    }

    @ApiOperation(value = "删除用户监控", notes = "", tags = {"OpenAPI"})
    @RequestMapping(value = "/monitor/{userId}/{clusterId}", method = RequestMethod.DELETE)
    public ActionResponse delMonitor(@PathVariable("userId") @Validated int userId,
            @PathVariable("clusterId") @Validated String cluserId) {
        return monitorService.delMonitor(userId, cluserId);
    }

}

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

import com.highgo.platform.apiserver.model.vo.request.ClusterVO;
import com.highgo.platform.apiserver.model.vo.response.ActionResponse;
import com.highgo.platform.operator.watcher.WatcherFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/${common.request-path-prefix}/${common.version}")
public class WatcherController {

    @Autowired
    private WatcherFactory watcherFactory;

    @RequestMapping(value = "/watcher/action/start", method = {RequestMethod.POST})
    public ActionResponse startWatcher(@Validated @RequestBody ClusterVO clusterVO) {
        boolean result = watcherFactory.startWatcherById(clusterVO.getClusterId());
        if (result) {
            return ActionResponse.actionSuccess();
        } else {
            return ActionResponse.actionFailed();
        }
    }

    @RequestMapping(value = "/watcher/action/stop", method = {RequestMethod.POST})
    public ActionResponse stopWatcher(@Validated @RequestBody ClusterVO clusterVO) {
        boolean result = watcherFactory.stopWatcherById(clusterVO.getClusterId());
        if (result) {
            return ActionResponse.actionSuccess();
        } else {
            return ActionResponse.actionFailed();
        }
    }
}

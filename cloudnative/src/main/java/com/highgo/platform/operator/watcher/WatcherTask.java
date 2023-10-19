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

package com.highgo.platform.operator.watcher;

import com.highgo.platform.apiserver.service.K8sClusterService;
import com.highgo.platform.operator.ElectLeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Profile(value = "prod")
public class WatcherTask {

    private static final Logger logger = LoggerFactory.getLogger(WatcherTask.class);

    @Resource
    private ElectLeader electLeader;

    @Resource
    private K8sClusterService k8sClusterService;

    @Resource
    private WatcherFactory watcherFactory;

    @Scheduled(fixedDelayString = "${common.refreshWatcherTaskTime:600000}")
    public void refreshWatcher() {
        if (!electLeader.isLeader) {
            logger.info("[WatcherTask.refreWatcher] I am slaver, will not refresh watcher.");
        } else {
            watcherFactory.refresh();
            logger.info("[WatcherTask.refreWatcher] I am master, refresh watcher done.");
        }

    }

}

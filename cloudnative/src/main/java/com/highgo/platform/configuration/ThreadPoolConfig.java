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

package com.highgo.platform.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 线程池管理配置
 * @author renlizou
 */
@Configuration
public class ThreadPoolConfig {

    @Bean("monitorExecutor")
    public Executor monitorExecutor() {
        ThreadPoolTaskExecutor monitorExecutor = new ThreadPoolTaskExecutor();
        monitorExecutor.setCorePoolSize(10);
        monitorExecutor.setMaxPoolSize(20);
        monitorExecutor.setQueueCapacity(100);
        monitorExecutor.setKeepAliveSeconds(60);
        monitorExecutor.setThreadNamePrefix("monitorExecutor");
        monitorExecutor.setWaitForTasksToCompleteOnShutdown(true);
        monitorExecutor.setAwaitTerminationSeconds(60);
        monitorExecutor.initialize();
        return monitorExecutor;
    }

    @Bean("autoScalingExecutor")
    public Executor autoScalingExecutor() {
        ThreadPoolTaskExecutor autoScalingExecutor = new ThreadPoolTaskExecutor();
        autoScalingExecutor.setCorePoolSize(10);
        autoScalingExecutor.setMaxPoolSize(20);
        autoScalingExecutor.setQueueCapacity(100);
        autoScalingExecutor.setKeepAliveSeconds(60);
        autoScalingExecutor.setThreadNamePrefix("autoScalingExecutor");
        autoScalingExecutor.setWaitForTasksToCompleteOnShutdown(true);
        autoScalingExecutor.setAwaitTerminationSeconds(60);
        autoScalingExecutor.initialize();
        return autoScalingExecutor;
    }

}

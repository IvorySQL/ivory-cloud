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

package com.highgo.platform.apiserver.model.vo.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author lucunqiao
 * @date 2023/2/14
 */
@Data
public class CreateMonitorVO {

    /**
     * user信息 创建监控
     */

    private int userId; // 用户id

    private String name; // 用户名

    private String namespace; // 命名空间

    private String monitorStatus; // 监控状态

    private String monitorUrl; // 监控url

    @NotBlank
    private String accessMode; // accessMode

    @NotBlank
    private String clusterId; // cluster id
}

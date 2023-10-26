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

package com.highgo.platform.apiserver.model.dto;

import com.highgo.cloud.enums.AutoScalingOperation;
import com.highgo.cloud.enums.AutoScalingStatus;
import com.highgo.cloud.enums.AutoScalingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.util.Date;

/**
 * @author: highgo-lucunqiao
 * @date: 2023/4/20 10:21
 * @Description: 自动弹性伸缩操作log类
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AutoScalingHistoryDTO implements Serializable {

    private String id;

    private String instanceId; // 实例id

    @Enumerated(EnumType.STRING)
    private AutoScalingType type; // 弹性伸缩资源类型

    @Enumerated(EnumType.STRING)
    private AutoScalingOperation operation; // 弹性伸缩操作

    private String alertMessage; // 告警信息

    @Enumerated(EnumType.STRING)
    private AutoScalingStatus status; // 本次操作状态

    private String operateLog; // 操作log

    private String sourceValue; // 操作之前的值

    private String targetValue; // 操作之后的值

    private Date createdAt;

    private Date updatedAt;

    private Date deletedAt;

    private Boolean isDeleted;

}

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

package com.highgo.platform.apiserver.model.po;

import com.highgo.cloud.enums.OperationStatus;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

/**
 * @author srk
 */
@Entity
@Table(name = "config_change_history")
@Data
public class ConfigChangeHistoryPO extends BaseEntity {

    /**
     * 实例ID
     */
    private String instanceId;
    /**
     * 变更描述
     */
    private String description;
    /**
     * 变更状态
     */
    @Enumerated(EnumType.STRING)
    private OperationStatus status;
}

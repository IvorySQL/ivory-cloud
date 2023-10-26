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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author srk
 * @version 1.0
 * @project micro_service_cloud
 * @description 数据库恢复记录表
 * @date 2023/9/25 17:26:24
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "restore_record")
@Builder
public class RestoreRecordPO extends BaseEntity {

    /**
     *  数据库实例id
     */
    private String instanceId;
    /**
     *  恢复开始时间
     */
    private String startTime;
    /**
     *  恢复结束时间
     */
    private String completionTime;
    /**
     *  是否恢复完成
     */
    private Boolean finished;
}

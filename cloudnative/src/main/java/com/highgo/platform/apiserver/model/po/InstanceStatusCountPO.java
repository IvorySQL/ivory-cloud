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

import com.highgo.cloud.enums.InstanceStatus;
import lombok.*;

/**
 *  映射数据库查询实例状态数量统计
 * @author srk
 */
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstanceStatusCountPO {

    /**
     * 实例状态
     */
    private InstanceStatus status;

    /**
     * 实例统计数量
     */
    private Integer num;

}

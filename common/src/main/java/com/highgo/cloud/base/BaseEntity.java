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

package com.highgo.cloud.base;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 通用字段， 实体通用的创建，修改，删除时间
 * @author zou
 * @date 2022年6月27日15:48:53
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(CloudPlatformEntityListener.class)
public abstract class BaseEntity implements Serializable {

    private static final long serialVersionUID = -5981097980916945570L;

    /**
     * 创建时间
     */
    private Timestamp createdTime;
    /**
     * 更新时间
     */
    private Timestamp updatedTime;
    /**
     * 删除时间
     */
    private Timestamp deletedTime;
    /**
     * 是否已删除
     */
    private int deleted = 0;

}

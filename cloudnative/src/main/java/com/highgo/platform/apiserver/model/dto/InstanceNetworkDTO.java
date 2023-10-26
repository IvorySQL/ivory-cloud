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

import com.highgo.cloud.enums.NetworkType;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.util.Date;

@Data
public class InstanceNetworkDTO implements Serializable {

    /**
     * 网络信息
     */
    private static final long serialVersionUID = -1666060312000L;

    private String instanceId;

    @Enumerated(EnumType.STRING)
    private NetworkType type; // 读写/只读

    private String nodeIp;

    private Integer nodePort;

    private String service; // 内网连接svc信息

    private Integer port; // 内网连接端口

    private Date createdAt;

    private Date updatedAt;

    private Date deletedAt;

    private Boolean isDeleted = false;

}

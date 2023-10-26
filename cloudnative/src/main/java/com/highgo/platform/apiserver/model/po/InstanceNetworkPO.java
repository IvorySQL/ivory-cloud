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

import com.highgo.cloud.enums.NetworkType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

/**
 * @author srk
 */
@Entity
@Table(name = "instance_network")
@ApiModel("Postgresql实例网络信息")
@Data
public class InstanceNetworkPO extends BaseEntity {

    private static final long serialVersionUID = -1665996121000L;

    private String instanceId;

    @ApiModelProperty(value = "读写信息(只读/读写)")
    @Enumerated(EnumType.STRING)
    private NetworkType type;

    private String nodeIp;

    private Integer nodePort;

    /**
     * 内网连接svc信息
     */
    private String service;

    /**
     * 内网连接端口
     */
    private Integer port;

}

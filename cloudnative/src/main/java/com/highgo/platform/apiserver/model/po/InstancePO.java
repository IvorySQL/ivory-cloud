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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.highgo.cloud.enums.InstanceStatus;
import com.highgo.cloud.enums.InstanceType;
import com.highgo.cloud.enums.SwitchStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
@Table(name = "instance")
@Setter
@Getter
public class InstancePO extends BaseEntity {

    private static final long serialVersionUID = -1665991201000L;

    @ApiModelProperty(value = "数据库实例名称", required = true)
    @NotBlank(message = "{}")
    @Pattern(regexp = "^[A-Za-z\\u4e00-\\u9fa5]+[\\u4e00-\\u9fa5_0-9A-Za-z._-]*$", message = "{}")
    @Size(min = 1, max = 2, message = "{}")
    private String name; // 实例名称

    @ApiModelProperty(value = "k8s集群ID", required = true)
    @NotBlank(message = "{}")
    @Size(min = 36, max = 36)
    private String clusterId; // 实例所在k8s集群的集群ID

    @ApiModelProperty(value = "k8s集群命名空间", required = true)
    @NotBlank(message = "{}")
    private String namespace; // 命名空间

    @ApiModelProperty(value = "实例描述", required = true)
    @NotBlank(message = "{}")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5_a-zA-Z0-9_.\\-]{2,128}$", message = "{}")
    @Size(min = 2, max = 128, message = "{}")
    private String description;

    @ApiModelProperty(value = "实例版本", required = true)
    @NotBlank(message = "{}")
    private String version; // 数据库实例版本

    @ApiModelProperty(value = "k8s存储类型")
    @NotBlank(message = "{}")
    private String storageClass; // k8s存储类型

    @ApiModelProperty(value = "实例类型")
    @NotBlank(message = "{}")
    @Enumerated(EnumType.STRING)
    private InstanceType type; // 实例类型 单节点/高可用

    @ApiModelProperty(value = "实例cpu核数")
    @Min(value = 1)
    private Integer cpu;

    @ApiModelProperty(value = "实例内存配置大小")
    @Min(value = 1)
    private Integer memory;

    @ApiModelProperty(value = "实例使用存储磁盘大小")
    @Min(value = 5)
    private Integer storage;

    @ApiModelProperty(value = "用户")
    private String account;

    @ApiModelProperty(value = "创建实例的用户")
    private String creator;

    @JsonIgnore
    // @ContextKey("accountId")
    private String rootUser; // 主用户

    @ApiModelProperty(value = "实例状态")
    @Enumerated(EnumType.STRING)
    private InstanceStatus status;// 实例状态

    @ApiModelProperty(value = "实例的管理员账户名")
    private String admin;

    @ApiModelProperty(value = "实例外网开启状态")
    @Enumerated(EnumType.STRING)
    private SwitchStatus nodePortSwitch = SwitchStatus.ON;
}

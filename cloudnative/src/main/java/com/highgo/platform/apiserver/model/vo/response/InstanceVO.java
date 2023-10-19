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

package com.highgo.platform.apiserver.model.vo.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.highgo.cloud.enums.InstanceStatus;
import com.highgo.cloud.enums.InstanceType;
import com.highgo.platform.apiserver.model.dto.InstanceNetworkDTO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Map;

/***
 * 实例详情
 * @author srk
 */

@Getter
@Setter
public class InstanceVO implements Serializable {

    private static final long serialVersionUID = -1666056832000L;
    /**
     *  实例id
     */
    private String id;
    /**
     *  实例名称
     */
    private String name;
    /**
     *  集群id
     */
    private String clusterId;

    private String namespace;

    private String description;

    private String version;

    private String storageClass;
    /**
     *  实例类型 单节点/高可用
     */
    private InstanceType type;

    private Integer cpu;

    private Integer memory;

    private Integer storage;

    private String creator;

    /**
     * 实例创建时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdAt;
    /**
     * 实例状态
     */
    private InstanceStatus status;
    /**
     *  管理员账号
     */
    private String admin;
    /**
     *  外网开关状态
     */
    private String nodePortSwitch;
    /**
     *  网络信息
     */
    private List<InstanceNetworkDTO> network;
    /**
     *  集群名称
     */
    private String clusterName;

    private Map<String, Object> extraMeta;

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        Field[] fields = this.getClass().getDeclaredFields();
        result.append(" {");
        for (Field field : fields) {
            result.append(", ");
            try {
                result.append(field.getName());
                result.append(":");
                result.append(field.get(this));
            } catch (IllegalAccessException ex) {
                System.out.println(ex);
            }
        }
        result.append(" }");
        return result.toString();
    }

}

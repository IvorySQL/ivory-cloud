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

import com.highgo.cloud.enums.InstanceType;
import lombok.Getter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.Map;

@Getter
public class CreateInstanceVO implements Serializable {

    /**
     * 创建实例入参
     */

    @NotBlank(message = "{}")
    @Pattern(regexp = "^[a-z][0-9a-z.-]{0,29}$", message = "{param.instance_name.invalid}")
    private String name; // 实例名称

    @NotBlank(message = "{param.cluster_id.invalid}")
    private String clusterId; // 实例所在k8s集群的集群ID

    @NotBlank(message = "{param.namespace.is_empty}")
    private String namespace; // 命名空间

    @Pattern(regexp = "^[\\u4e00-\\u9fa5_a-zA-Z0-9_.\\-]{0,128}$", message = "{param.instance_description.invalid}")
    private String description;

    @NotBlank(message = "{param.instance_version.is_empty}")
    private String version; // 数据库实例版本

    @NotBlank(message = "{param.instance_storage_class.is_empty}")
    private String storageClass; // k8s存储类型

    @Enumerated(EnumType.STRING)
    private InstanceType type = InstanceType.ALONE; // 实例类型 单节点/高可用

    @Pattern(regexp = "^[a-z]{1,16}$", message = "{param.instance_admin.invalid}")
    @NotBlank
    private String admin; // 管理员账户名称

    // @Pattern(regexp =
    // "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d!%\\\"\\$\\+\\|\\^\\'\\{\\}\\[\\],/:;<=>?_~`]{8,32}$", message =
    // "{param.instance_password.invalid}")
    @NotBlank
    @Pattern(regexp = "^(?=.*[\\!@#\\$%\\^&\\*\\(\\)])(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d\\!@#\\$%\\^&\\*\\(\\)]{8,32}$", message = "{param.instance_password.invalid}")
    private String password; // 管理员账户密码 8-32个字符，必须包含大小写字母和数字，支持英文特殊字符!"$%+,/:;<=>?[]^_`{}|~

    @Min(value = 1)
    private Integer cpu = 1;

    @Min(value = 1)
    private Integer memory = 1;

    @Min(value = 1)
    private Integer storage = 1;

    private String originalBackupId; // 备份ID，恢复至新实例时使用

    private String originalInstanceId; // 源实例ID，binlog恢复至新实例时使用

    private Date restoreTime; // 恢复至指定时间

    private Map<String, Object> extraMeta;

    private String creator; // 创建的用户id

    private String creatorName; // 创建用户的名字

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        Field[] fields = this.getClass().getDeclaredFields();
        result.append(" {");
        for (Field field : fields) {
            result.append(", ");
            try {
                if ("password".equals(field.getName())) {
                    continue;
                }
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

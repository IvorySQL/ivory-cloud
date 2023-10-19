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

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.lang.reflect.Field;

@Getter
public class VerifyInstanceNameVO implements Serializable {

    @NotBlank(message = "{params.instance_cluster_id.invalid}")
    private String clusterId; // 实例所在k8s集群的集群ID

    @NotBlank(message = "{}")
    private String namespace; // 命名空间

    @NotBlank(message = "{}")
    @Pattern(regexp = "^[A-Za-z\\u4e00-\\u9fa5]+[\\u4e00-\\u9fa5_0-9A-Za-z._-]{2,7}$", message = "{params.instance_name.invalid}")
    @Size(min = 1, max = 8, message = "{}")
    private String name; // 实例名称

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

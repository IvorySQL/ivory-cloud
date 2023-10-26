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

import com.highgo.cloud.enums.BackupMethod;
import com.highgo.cloud.enums.BackupMode;
import com.highgo.cloud.enums.BackupType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.lang.reflect.Field;

@Getter
@Setter
public class CreateBackupVO implements Serializable {

    @Pattern(regexp = "^[\\u4e00-\\u9fa5_a-zA-Z0-9_.:\\-]{2,128}$", message = "{param.backup_name.invalid}")
    private String name;

    @Pattern(regexp = "^[\\u4e00-\\u9fa5_a-zA-Z0-9_.\\-]{0,128}$", message = "{param.instance_description.invalid}")
    private String description;

    private BackupType backupType = BackupType.PHYSICAL;

    private BackupMode backupMode = BackupMode.FULL;

    private BackupMethod backupMethod = BackupMethod.MANUAL;

    private String id;

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

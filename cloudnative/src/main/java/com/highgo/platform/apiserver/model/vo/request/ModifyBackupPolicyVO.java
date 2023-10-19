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

import com.highgo.cloud.enums.BackupMode;
import com.highgo.cloud.enums.BackupType;
import com.highgo.cloud.enums.SwitchStatus;
import lombok.Getter;

import java.io.Serializable;
import java.lang.reflect.Field;

@Getter
public class ModifyBackupPolicyVO implements Serializable {

    /**
     * 修改备份策略参数
     */
    private SwitchStatus status;

    private BackupType backupType;

    private BackupMode backupMode;

    private String startTime;

    private String backupPeriod; // 备份周期 Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday

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

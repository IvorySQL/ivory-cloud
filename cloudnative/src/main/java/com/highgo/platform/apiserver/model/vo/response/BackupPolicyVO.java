package com.highgo.platform.apiserver.model.vo.response;


import com.highgo.cloud.enums.BackupMode;
import com.highgo.cloud.enums.BackupType;
import com.highgo.cloud.enums.SwitchStatus;
import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Field;

@Data
public class BackupPolicyVO implements Serializable {

    /**
     * 备份策略信息
     */
    private String instanceId;

    private BackupType backupType;

    private BackupMode backupMode;

    private String startTime;

    private String backupPeriod;

    private SwitchStatus status; //备份是否开启

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

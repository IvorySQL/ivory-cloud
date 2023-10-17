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

    private String backupPeriod; //备份周期  Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday

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

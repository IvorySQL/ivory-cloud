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

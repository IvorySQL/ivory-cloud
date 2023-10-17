package com.highgo.platform.apiserver.model.vo.request;


import com.highgo.cloud.enums.SwitchStatus;
import lombok.Getter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.lang.reflect.Field;

@Getter
public class ModifyAutoBackupSwitchVO implements Serializable {

    /**
     * 自动备份开关
     */
    @Enumerated(EnumType.STRING)
    private SwitchStatus switchStatus; // 开关状态

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

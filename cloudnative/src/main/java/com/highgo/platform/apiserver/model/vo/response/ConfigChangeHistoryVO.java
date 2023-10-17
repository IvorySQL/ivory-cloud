package com.highgo.platform.apiserver.model.vo.response;


import com.highgo.cloud.enums.OperationStatus;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;

@Data
public class ConfigChangeHistoryVO implements Serializable {

    /**
     * 配置参数修改历史
     */
    private String id;

    private String instanceId;

    private String description;

    private Date createdAt; // 修改时间

    @Enumerated(EnumType.STRING)
    private OperationStatus status;

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

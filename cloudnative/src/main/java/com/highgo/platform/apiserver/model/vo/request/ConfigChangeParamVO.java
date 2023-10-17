package com.highgo.platform.apiserver.model.vo.request;


import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Field;

@Data
public class ConfigChangeParamVO implements Serializable {


    private String paramName; // 参数名称

    private String sourceValue; // 参数原始值

    private String targetValue; // 参数修改后目标值

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

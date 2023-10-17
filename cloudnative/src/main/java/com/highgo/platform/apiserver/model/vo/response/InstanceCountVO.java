package com.highgo.platform.apiserver.model.vo.response;

import lombok.*;

import java.lang.reflect.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstanceCountVO {

    private int startingCount;

    private int runningCount;

    private int errorCount;

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




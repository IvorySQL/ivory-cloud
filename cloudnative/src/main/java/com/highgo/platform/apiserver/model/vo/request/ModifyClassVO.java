package com.highgo.platform.apiserver.model.vo.request;

import lombok.Getter;

import javax.validation.constraints.Min;
import java.io.Serializable;
import java.lang.reflect.Field;

@Getter
public class ModifyClassVO implements Serializable {

    /**
     * 规格变更参数
     */

    @Min(value = 1)
    private Integer cpu;

    @Min(value = 1)
    private Integer memory;

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

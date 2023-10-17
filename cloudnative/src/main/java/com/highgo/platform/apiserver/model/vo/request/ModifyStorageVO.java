package com.highgo.platform.apiserver.model.vo.request;

import lombok.Getter;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.lang.reflect.Field;

@Getter
public class ModifyStorageVO implements Serializable {

    /**
     * 实例磁盘扩展参数
     */
    @NotNull
    @Min(value = 1)
    private Integer storageSize;

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

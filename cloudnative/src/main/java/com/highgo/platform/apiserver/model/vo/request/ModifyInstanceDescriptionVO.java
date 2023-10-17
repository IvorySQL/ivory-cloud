package com.highgo.platform.apiserver.model.vo.request;

import lombok.Getter;

import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.lang.reflect.Field;

@Getter
public class ModifyInstanceDescriptionVO implements Serializable {

    /**
     * 修改实例描述
     */
    private static final long serialVersionUID = 1666079468000L;

    @Pattern(regexp = "^[\\u4e00-\\u9fa5_a-zA-Z0-9_.\\-]{0,128}$", message = "{param.instance_description.invalid}")
    private String description;

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

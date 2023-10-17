package com.highgo.platform.apiserver.model.vo.request;

import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

@Getter
public class ModifyConfigChangeVO implements Serializable {



    /**
     * 修改配置参数
     */
    @Size(max = 255, message = "{param.config_description.invalid}")
    private String description;

    @NotEmpty(message = "")
    private List<ConfigChangeParamVO> params; // 修改的参数{name:value}

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

package com.highgo.platform.apiserver.model.vo.request;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.lang.reflect.Field;

@Getter
public class VerifyInstanceNameVO implements Serializable {

    @NotBlank(message = "{params.instance_cluster_id.invalid}")
    private String clusterId; // 实例所在k8s集群的集群ID

    @NotBlank(message = "{}")
    private String namespace; // 命名空间

    @NotBlank(message = "{}")
    @Pattern(regexp = "^[A-Za-z\\u4e00-\\u9fa5]+[\\u4e00-\\u9fa5_0-9A-Za-z._-]{2,7}$", message = "{params.instance_name.invalid}")
    @Size(min = 1, max = 8, message = "{}")
    private String name; // 实例名称

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

package com.highgo.platform.apiserver.model.vo.response;


import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Field;

@Data
public class InstanceEventVO implements Serializable {

    private String instanceId; // 实例ID

    private Integer nodeNum; // 实例副本数量

    private Integer nodeReadyNum; // 运行中副本数量

    private String stsevent; // statefulset 事件

    private String podevent; // pod 事件

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

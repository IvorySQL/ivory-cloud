package com.highgo.cloud.base;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.Timestamp;

/**
 * @author: highgo-lucunqiao
 * @date: 2023/8/9 16:42
 * @Description:
 */
@Getter
@Setter
public class BaseDTO implements Serializable {
    private static final long serialVersionUID = 5019391074210438957L;
    /**
     * 创建时间
     */
    private Timestamp createdTime;
    /**
     * 更新时间
     */
    private Timestamp updatedTime;
    /**
     * 删除时间
     */
    private Timestamp deletedTime;
    /**
     * 是否已删除
     */
    private int deleted = 0;

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        Field[] fields = this.getClass().getDeclaredFields();
        try {
            for (Field f : fields) {
                f.setAccessible(true);
                builder.append(f.getName(), f.get(this)).append("\n");
            }
        } catch (Exception e) {
            builder.append("toString builder encounter an error");
        }
        return builder.toString();
    }
}

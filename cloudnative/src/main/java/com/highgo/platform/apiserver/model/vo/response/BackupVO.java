package com.highgo.platform.apiserver.model.vo.response;


import com.highgo.cloud.enums.BackupMethod;
import com.highgo.cloud.enums.BackupMode;
import com.highgo.cloud.enums.BackupStatus;
import com.highgo.cloud.enums.BackupType;
import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;

@Data
public class BackupVO implements Serializable {

    private String id; // 备份id

    private String instanceId; // 实例id

    private String name; // 备份名称

    private String description; // 备份描述

    private BackupType backupType; // 备份类型 物理/逻辑

    private BackupMode backupMode; // 备份模式 全量/增量

    private BackupMethod backupMethod; // 备份方式

    private BackupStatus status; // 备份状态

    private Boolean isRestoring = false; // 恢复中

    private String fileName; // 备份文件名称

    private Date createdAt; // 备份创建时间

    private Date lastRecoveryTime; // 最后一次恢复时间

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

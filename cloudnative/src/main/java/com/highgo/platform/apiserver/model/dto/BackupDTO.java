package com.highgo.platform.apiserver.model.dto;


import com.highgo.cloud.enums.BackupMethod;
import com.highgo.cloud.enums.BackupMode;
import com.highgo.cloud.enums.BackupStatus;
import com.highgo.cloud.enums.BackupType;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.util.Date;

@Data
public class BackupDTO implements Serializable {

    private String id;

    private String instanceId; // 实例ID

    private String name; // 备份名称

    private String description; // 备份描述

    @Enumerated(EnumType.STRING)
    private BackupType backupType; //备份类型 物理备份/逻辑备份

    @Enumerated(EnumType.STRING)
    private BackupMode backupMode; //备份模式 全量备份/增量备份

    @Enumerated(EnumType.STRING)
    private BackupMethod backupMethod; // 备份方式 手动备份/自动备份

    @Enumerated(EnumType.STRING)
    private BackupStatus status; // 备份状态

    private Date lastRecoveryTime; // 最后一次恢复时间

    private Boolean isRestoring = false; // 恢复中

    private String fileName;

    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
    private Boolean isDeleted;

}

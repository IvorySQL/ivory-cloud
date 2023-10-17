package com.highgo.platform.apiserver.model.po;


import com.highgo.cloud.enums.BackupMethod;
import com.highgo.cloud.enums.BackupMode;
import com.highgo.cloud.enums.BackupStatus;
import com.highgo.cloud.enums.BackupType;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Date;


@Data
@Entity
@Table(name = "backup")
public class BackupPO extends BaseEntity {

    private static final long serialVersionUID = -1665998419000L;

    /**
     * 实例ID
     */
    private String instanceId;
    /**
     * 备份名称
     */
    @Pattern(regexp = "^[\\u4e00-\\u9fa5_a-zA-Z0-9_.\\-]{2,128}$", message = "{}")
    @NotBlank(message = "{}")
    private String name;
    /**
     * 备份描述
     */
    @Pattern(regexp = "^[\\u4e00-\\u9fa5_a-zA-Z0-9_.\\-]{2,128}$", message = "{}")
    private String description;
    /**
     * 备份类型 物理备份/逻辑备份
     */
    @Enumerated(EnumType.STRING)
    @NotBlank(message = "{}")
    private BackupType backupType;
    /**
     * 备份模式 全量备份/增量备份
     */
    @Enumerated(EnumType.STRING)
    @NotBlank(message = "{}")
    private BackupMode backupMode;
    /**
     * 备份方式 手动备份/自动备份
     */
    @Enumerated(EnumType.STRING)
    private BackupMethod backupMethod;
    /**
     * 备份状态
     */
    @Enumerated(EnumType.STRING)
    private BackupStatus status;
    /**
     * 备份文件名称
     */
    private String fileName;
    /**
     * 最后一次恢复时间
     */
    private Date lastRecoveryTime;
    /**
     * 恢复中
     */
    private Boolean isRestoring = false;

}

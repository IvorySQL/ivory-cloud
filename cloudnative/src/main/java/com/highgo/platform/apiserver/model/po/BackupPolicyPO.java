package com.highgo.platform.apiserver.model.po;

import com.highgo.cloud.enums.BackupMode;
import com.highgo.cloud.enums.BackupType;
import com.highgo.cloud.enums.SwitchStatus;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Pattern;

@Entity
@Data
@Table(name = "backup_policy")
public class BackupPolicyPO extends BaseEntity {

    /**
     * 实例ID
     */
    private String instanceId;
    /**
     * 开始备份时间
     */
    @Pattern(regexp = "^(([0-1]\\d)|(2[0-4])):00$", message = "{}")
    private String startTime = "00:00";
    /**
     * 备份周期
     */
    @Pattern(regexp = "^((Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday),)*(Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday)?$", message = "{rds.mysql.param.backup_period.invalid}")
    private String backupPeriod = "Monday,Tuesday,Wednesday,Thursday,Friday,Saturday,Sunday";
    /**
     * 备份类型 物理备份/逻辑备份
     */
    @Enumerated(EnumType.STRING)
    private BackupType backupType = BackupType.PHYSICAL;
    /**
     * 备份模式 全量备份/增量备份
     */
    @Enumerated(EnumType.STRING)
    private BackupMode backupMode = BackupMode.FULL;
    /**
     * 自动备份开启状态
     */
    private SwitchStatus status = SwitchStatus.OFF;

}

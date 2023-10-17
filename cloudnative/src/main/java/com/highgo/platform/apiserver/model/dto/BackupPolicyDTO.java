package com.highgo.platform.apiserver.model.dto;

import com.highgo.cloud.enums.BackupMode;
import com.highgo.cloud.enums.BackupType;
import com.highgo.cloud.enums.SwitchStatus;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
public class BackupPolicyDTO implements Serializable {

    private static final long serialVersionUID = 2786199374922135560L;

    private String instanceId; // 实例ID

    @Pattern(regexp = "^(([0-1]\\d)|(2[0-4])):00$", message = "{}")
    private String startTime = "00:00"; // 开始备份时间

    @Pattern(regexp = "^((Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday),)*(Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday)?$", message = "{rds.mysql.param.backup_period.invalid}")
    private String backupPeriod = "Monday,Tuesday,Wednesday,Thursday,Friday,Saturday,Sunday"; // 备份周期

    @Enumerated(EnumType.STRING)
    private BackupType backupType = BackupType.PHYSICAL; // 备份类型 物理备份/逻辑备份

    @Enumerated(EnumType.STRING)
    private BackupMode backupMode = BackupMode.FULL; // 备份模式 全量备份/增量备份

    @Transient
    private SwitchStatus status = SwitchStatus.OFF; // 自动备份开启状态

}

package com.highgo.platform.apiserver.model.dto;

import com.highgo.platform.apiserver.model.po.BackupPolicyPO;
import com.highgo.cloud.enums.InstanceStatus;
import com.highgo.cloud.enums.InstanceType;
import com.highgo.cloud.enums.SwitchStatus;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Pattern;
import java.util.*;

@Data
public class InstanceDTO {

    private String id;

    private String name; // 实例名称

    private String clusterId; // 实例所在k8s集群的集群ID

    private String namespace; // 命名空间

    private String description;

    private String version; // 数据库实例版本

    private String storageClass; // k8s存储类型

    private InstanceType type; // 实例类型 单节点/高可用

    private Integer cpu;

    private Integer memory;

    private Integer storage;

    private String account;

    private String creator;

    private String creatorName; //创建用户的名字

    private String rootUser; // 主用户

    private String password;

    @Enumerated(EnumType.STRING)
    private InstanceStatus status;// 实例状态

    private String admin;

    @Enumerated(EnumType.STRING)
    private SwitchStatus nodePortSwitch = SwitchStatus.ON;

    private Date createdAt;

    private Date updatedAt;

    private Date deletedAt;

    private Boolean isDeleted = false;

    private String originalBackupId; // 备份ID，恢复至新实例时使用

    private String originalInstanceId; // 源实例ID，binlog恢复至新实例时使用

    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z|^$")
    private String restoreTime; // 恢复至指定时间

    private BackupPolicyPO backupPolicyPO;

    private List<ConfigInstanceParamDTO> configInstanceParamDTOS;

    private String configChangeHistoryId;

    private List<InstanceNetworkDTO> network = new ArrayList<>();

    private Map<String, Object> extraMeta;
}

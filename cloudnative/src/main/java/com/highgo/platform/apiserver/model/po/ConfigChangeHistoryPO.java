package com.highgo.platform.apiserver.model.po;

import com.highgo.cloud.enums.OperationStatus;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

/**
 * @author srk
 */
@Entity
@Table(name = "config_change_history")
@Data
public class ConfigChangeHistoryPO extends BaseEntity {

    /**
     * 实例ID
     */
    private String instanceId;
    /**
     * 变更描述
     */
    private String description;
    /**
     * 变更状态
     */
    @Enumerated(EnumType.STRING)
    private OperationStatus status;
}

package com.highgo.platform.apiserver.model.po;

import com.highgo.cloud.enums.AutoScalingOperation;
import com.highgo.cloud.enums.AutoScalingStatus;
import com.highgo.cloud.enums.AutoScalingType;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

/**
 * @author: highgo-lucunqiao
 * @date: 2023/4/19 18:21
 * @Description: 自动弹性伸缩操作log类
 */
@Entity
@Data
@Table(name = "autoscaling_history")
public class AutoScalingHistoryPO extends BaseEntity{

    /**
     * 实例id
     */
    private String instanceId;

    /**
     * 弹性伸缩资源类型
     */
    @Enumerated(EnumType.STRING)
    private AutoScalingType type;
    /**
     * 弹性伸缩操作
     */
    @Enumerated(EnumType.STRING)
    private AutoScalingOperation operation;
    /**
     * 告警信息
     */
    private String alertMessage;
    /**
     * 本次操作状态
     */
    @Enumerated(EnumType.STRING)
    private AutoScalingStatus status;
    /**
     * 操作log
     */
    private String operateLog;
    /**
     * 操作之前的值
     */
    private String sourceValue;
    /**
     * 操作之后的值
     */
    private String targetValue;

}

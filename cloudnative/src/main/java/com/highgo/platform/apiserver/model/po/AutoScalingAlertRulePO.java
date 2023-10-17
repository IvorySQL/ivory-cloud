package com.highgo.platform.apiserver.model.po;

import com.highgo.cloud.enums.AutoScalingConditionals;
import com.highgo.cloud.enums.AutoScalingOperation;
import com.highgo.cloud.enums.AutoScalingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

/**
 * @author: highgo-lucunqiao
 * @date: 2023/4/23 9:01
 * @Description: 自动弹性伸缩告警规则
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "autoscaling_alert_rule")
@Builder
public class AutoScalingAlertRulePO extends BaseEntity{

    /**
     * 集群id
     */
    private String clusterId;
    /**
     * 用户id
     */
    private String userId;
    /**
     * 告警名称
     */
    private String alertName;
    /**
     * 弹性伸缩资源类型
     */
    @Enumerated(EnumType.STRING)
    private AutoScalingType type;
    /**
     * 大于、小于、等于...
     */
    @Enumerated(EnumType.STRING)
    private AutoScalingConditionals conditionals;
    /**
     * 阈值
     */
    private Integer threshold;
    /**
     * 弹性伸缩操作
     */
    @Enumerated(EnumType.STRING)
    private AutoScalingOperation autoscaling;
    /**
     * 持续时间
     */
    private long duration;

}

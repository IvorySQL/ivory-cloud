package com.highgo.platform.apiserver.model.po;

import com.highgo.cloud.enums.SwitchStatus;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

/**
 * @author: highgo-lucunqiao
 * @date: 2023/4/23 9:00
 * @Description: 自动弹性伸缩开关
 */
@Entity
@Data
@Table(name = "autoscaling_switch")
public class AutoScalingSwitchPO extends BaseEntity{

    /**
     *  自动弹性伸缩开关
     */
    @Enumerated(EnumType.STRING)
    private SwitchStatus autoscalingSwitch = SwitchStatus.OFF;
    /**
     *  用户id
     */
    private String userId;
    /**
     *  集群id
     */
    private String clusterId;
}

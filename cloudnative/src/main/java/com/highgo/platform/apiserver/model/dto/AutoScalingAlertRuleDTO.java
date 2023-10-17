package com.highgo.platform.apiserver.model.dto;

import com.highgo.cloud.enums.AutoScalingConditionals;
import com.highgo.cloud.enums.AutoScalingOperation;
import com.highgo.cloud.enums.AutoScalingType;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;

/**
 * @author: highgo-lucunqiao
 * @date: 2023/4/23 9:01
 * @Description: 自动弹性伸缩告警规则
 */
@Data
public class AutoScalingAlertRuleDTO {

    private String id;

    private String clusterId; // 集群id

    private String alertName; // 告警名称

    private int userId; // 用户id

    @Enumerated(EnumType.STRING)
    private AutoScalingType type; //弹性伸缩资源类型

    @Enumerated(EnumType.STRING)
    private AutoScalingConditionals conditionals; // 大于、小于、等于...

    private Integer threshold; // 阈值

    @Enumerated(EnumType.STRING)
    private AutoScalingOperation autoscaling; // 弹性伸缩操作

    private long duration; // 持续时间

    private Date createdAt;

    private Date updatedAt;

    private Date deletedAt;

    private Boolean isDeleted;

}

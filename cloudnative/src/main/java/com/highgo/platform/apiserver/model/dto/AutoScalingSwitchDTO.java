package com.highgo.platform.apiserver.model.dto;

import com.highgo.cloud.enums.SwitchStatus;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;

/**
 * @author: highgo-lucunqiao
 * @date: 2023/4/23 9:00
 * @Description: 自动弹性伸缩开关
 */
@Data
public class AutoScalingSwitchDTO{

    private String id;//id

    private String userId;//用户id

    private String clusterId;//集群id

    @Enumerated(EnumType.STRING)
    private SwitchStatus autoscalingSwitch = SwitchStatus.OFF;//自动弹性伸缩开关

    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
    private Boolean isDeleted;
}

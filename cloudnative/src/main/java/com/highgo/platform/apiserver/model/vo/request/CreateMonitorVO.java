package com.highgo.platform.apiserver.model.vo.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author lucunqiao
 * @date 2023/2/14
 */
@Data
public class CreateMonitorVO {

    /**
     * user信息 创建监控
     */

    private int userId; // 用户id

    private String name; //用户名

    private String namespace; // 命名空间

    private String monitorStatus; // 监控状态

    private String monitorUrl; // 监控url

    @NotBlank
    private String accessMode; // accessMode

    @NotBlank
    private String clusterId; //cluster id
}

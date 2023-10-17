package com.highgo.platform.apiserver.model.vo.response;


import lombok.Data;

import java.io.Serializable;

@Data
public class ConfigChangeVO implements Serializable {

    /**
     * 配置参数信息
     */
    private String configChangeHistoryId; // 变更历史id

    private String paramName; // 参数名称

    private String sourceValue; // 参数原始值

    private String targetValue; // 参数修改后目标值


}

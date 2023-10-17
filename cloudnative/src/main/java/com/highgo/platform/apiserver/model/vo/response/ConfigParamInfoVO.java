package com.highgo.platform.apiserver.model.vo.response;


import com.highgo.cloud.enums.ConfigParamType;
import lombok.Data;

import java.io.Serializable;

@Data
public class ConfigParamInfoVO implements Serializable {

    /**
     * 配置参数信息
     */
    private String name;

    private ConfigParamType paramType;

    private String defaultValue; //默认值

    private String runningValue; // 运行值

    private String min;

    private String max;

    private String enumValue;

    private String description;

    private String rule;


}

package com.highgo.platform.apiserver.model.po;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author srk
 */
@Entity
@Data
@Table(name = "config_param_defination")
public class ConfigParamDefinationPO extends BaseEntity {

    /**
     * 参数名称
     */
    private String name;
    /**
     * 参数类型 枚举、数字、字符串...
     */
    private String paramType;
    /**
     * 默认值
     */
    private String defaultValue;
    /**
     * 最小值
     */
    private String min;
    /**
     * 最大值
     */
    private String max;
    /**
     * 枚举值
     */
    private String enumValue;
    /**
     * 规则
     */
    private String rule;
    /**
     * 描述
     */
    private String description;


}

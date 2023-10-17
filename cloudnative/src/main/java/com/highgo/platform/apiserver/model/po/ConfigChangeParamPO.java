package com.highgo.platform.apiserver.model.po;


import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author srk
 */
@Entity
@Data
@Table(name = "config_change_param")
public class ConfigChangeParamPO extends BaseEntity {

    private static final long serialVersionUID = -1666054560000L;

    /**
     * 变更历史id
     */
    private String configChangeHistoryId; 
    /**
     * 参数名称
     */
    private String paramName; 
    /**
     * 参数原始值
     */
    private String sourceValue; 
    /**
     * 参数修改后目标值
     */
    private String targetValue; 


}

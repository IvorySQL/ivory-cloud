package com.highgo.platform.apiserver.model.po;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "config_instance_param")
public class ConfigInstanceParamPO extends BaseEntity {

    private static final long serialVersionUID = 5820763066073643122L;
    private String instanceId;
    private String name;
    private String value;
    private String type;
}

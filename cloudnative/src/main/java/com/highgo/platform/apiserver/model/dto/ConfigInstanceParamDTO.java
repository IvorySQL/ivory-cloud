package com.highgo.platform.apiserver.model.dto;

import com.highgo.platform.apiserver.model.po.BaseEntity;
import lombok.Data;

@Data
public class ConfigInstanceParamDTO extends BaseEntity {

    private String instanceId;
    private String name;
    private String value;
    private String type;
}

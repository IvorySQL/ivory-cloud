package com.highgo.platform.operator.cr.bean.instance;

import lombok.Data;

@Data
public class StatusInstance {

    private String name;

    private Integer readyReplicas;

    private Integer replicas;

    private Integer updatedReplicas;
}

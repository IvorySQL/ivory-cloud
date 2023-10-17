package com.highgo.platform.apiserver.model.vo.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ClusterVO {

    @NotBlank(message = "{param.cluster_id.invalid}")
    private String clusterId;
}

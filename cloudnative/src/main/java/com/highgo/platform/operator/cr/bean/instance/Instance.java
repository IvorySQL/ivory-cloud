package com.highgo.platform.operator.cr.bean.instance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.highgo.platform.operator.cr.bean.common.Resource;
import com.highgo.platform.operator.cr.bean.common.VolumeClaimSpec;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Instance {

    /**
     * 副本数
     */
    private int replicas;

    private VolumeClaimSpec dataVolumeClaimSpec;

    @Value("${common.serviceName}")
    private String name;

    private Resource resources;
}

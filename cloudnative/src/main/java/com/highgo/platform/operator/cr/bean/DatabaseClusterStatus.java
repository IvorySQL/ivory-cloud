package com.highgo.platform.operator.cr.bean;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.highgo.platform.operator.cr.bean.instance.StatusInstance;
import com.highgo.platform.operator.cr.bean.status.PgbackrestStatus;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
@Data
public class DatabaseClusterStatus {

    private List<StatusInstance> instances;
    private PgbackrestStatus pgbackrest;
}

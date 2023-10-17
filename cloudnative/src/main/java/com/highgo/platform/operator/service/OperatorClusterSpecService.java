package com.highgo.platform.operator.service;


import com.highgo.platform.apiserver.model.dto.InstanceDTO;
import com.highgo.platform.operator.cr.bean.DatabaseClusterSpec;

public interface OperatorClusterSpecService {

    /**
     * 构建postgrescluster cr spec
     * @param instanceDTO
     * @return
     */
    public DatabaseClusterSpec initClusterSpec(InstanceDTO instanceDTO);
}

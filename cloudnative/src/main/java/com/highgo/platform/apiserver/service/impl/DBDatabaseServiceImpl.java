package com.highgo.platform.apiserver.service.impl;

import com.highgo.platform.apiserver.model.vo.request.DatabaseVO;
import com.highgo.platform.apiserver.model.dto.InstanceDTO;
import com.highgo.platform.apiserver.model.vo.response.ActionResponse;
import com.highgo.platform.apiserver.service.DBDatabaseService;
import com.highgo.platform.apiserver.service.InstanceService;
import com.highgo.platform.operator.service.OperatorUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lucunqiao
 * @date 2022/12/14
 */
@Service
public class DBDatabaseServiceImpl implements DBDatabaseService {
    private static final Logger logger = LoggerFactory.getLogger(DBDatabaseServiceImpl.class);

    @Autowired
    private InstanceService instanceService;

    @Autowired
    private OperatorUserService operatorUserService;

    @Override
    public ActionResponse createDatabase(String instanceId, DatabaseVO databaseVO) {

        InstanceDTO instanceDTO = instanceService.beforeOperateInstance(instanceId);
        operatorUserService.createDatabase(instanceDTO.getClusterId(), instanceDTO.getNamespace(), instanceDTO.getName(), databaseVO);
        return ActionResponse.actionSuccess();
    }

    @Override
    public ActionResponse deleteDatabase(String instanceId, String dbname) {
        return null;
    }

    @Override
    public List<DatabaseVO> listDatabases(String instanceId) {
        return null;
    }
}

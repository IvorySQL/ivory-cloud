package com.highgo.platform.apiserver.service.impl;

import com.highgo.platform.apiserver.model.vo.request.DatabaseUserVO;
import com.highgo.platform.apiserver.model.dto.InstanceDTO;
import com.highgo.platform.apiserver.model.vo.request.ResetPasswordVO;
import com.highgo.platform.apiserver.model.vo.response.ActionResponse;
import com.highgo.platform.apiserver.service.DBUserService;
import com.highgo.platform.apiserver.service.InstanceService;
import com.highgo.platform.operator.service.OperatorUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DBUserServiceImpl implements DBUserService {

    @Autowired
    private InstanceService instanceService;

    @Autowired
    private OperatorUserService operatorUserService;

    /**
     * 重置用户密码
     *
     * @param instanceId 实例id
     * @param username   用户名
     * @return
     */
    @Override
    public ActionResponse resetPassword(String instanceId, String username, ResetPasswordVO resetPasswordVO) {
        InstanceDTO instanceDTO = instanceService.beforeOperateInstance(instanceId);
        operatorUserService.resetPassword(instanceDTO.getClusterId(), instanceDTO.getNamespace(), instanceDTO.getName(), username, resetPasswordVO.getPassword());
        return ActionResponse.actionSuccess();
    }

    @Override
    public ActionResponse lockDbUser(String instanceId, String userName, String lock) {
        return null;
    }

    @Override
    public ActionResponse createDBUser(String instanceId, DatabaseUserVO databaseUserVO) {
        InstanceDTO instanceDTO = instanceService.beforeOperateInstance(instanceId);
        operatorUserService.createUser(instanceDTO.getClusterId(), instanceDTO.getNamespace(), instanceDTO.getName(), databaseUserVO);
        return ActionResponse.actionSuccess();
    }

    @Override
    public ActionResponse deleteDbUser(String instanceId, String userName) {
        InstanceDTO instanceDTO = instanceService.beforeOperateInstance(instanceId);
        operatorUserService.deleteDbUser(instanceDTO.getClusterId(), instanceDTO.getNamespace(), instanceDTO.getName(), userName);
        return ActionResponse.actionSuccess();
    }

    @Override
    public List<DatabaseUserVO> listDbUsers(String instanceId) {
        return null;
    }
}

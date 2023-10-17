package com.highgo.platform.apiserver.service;


import com.highgo.platform.apiserver.model.vo.request.DatabaseUserVO;
import com.highgo.platform.apiserver.model.vo.request.ResetPasswordVO;
import com.highgo.platform.apiserver.model.vo.response.ActionResponse;

import java.util.List;

public interface DBUserService {

    /**
     * 重置用户密码
     * @param instanceId 实例id
     * @param username 用户名
     * @return
     */
    ActionResponse resetPassword(String instanceId, String username, ResetPasswordVO resetPasswordVO);

    /**
     * 锁定数据库用户
     *
     * @param instanceId
     * @param userName
     * @param lock
     * @return
     */
    ActionResponse lockDbUser(String instanceId, String userName, String lock);


    /**
     * 创建数据库用户
     *
     * @param instanceId 实例id
     * @param databaseUserVO 数据库用户
     * @return
     */
    ActionResponse createDBUser(String instanceId, DatabaseUserVO databaseUserVO);


    /**
     * 删除数据库用户
     * @param instanceId 实例id
     * @param userName 数据库用户名
     * @return
     */
    ActionResponse deleteDbUser(String instanceId, String userName);

    /**
     * 获取用户列表
     *
     * @param instanceId
     * @return
     */
    List<DatabaseUserVO> listDbUsers(String instanceId);

}

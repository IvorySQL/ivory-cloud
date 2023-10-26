/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

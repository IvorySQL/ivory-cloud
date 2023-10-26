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

package com.highgo.cloud.auth.constant;

/**
 * 用户常量类
 * @author chushaolin
 *
 */
public class UserConstants {

    /**
     * 
          *  超级用户：可以配置k8s服务器
     */
    public static final String ROLE_ADMIN_NAME = "admin";

    /**
     * 
          *  超级用户：可以配置k8s服务器
     */
    public static final int ROLE_ADMIN_ID = 2;

    /**
     * 
         * 普通用户：只能查看K8S服务器
     */
    public static final int AUTHORITY_ROUTINE_ID = 1;

    /**     
          *  普通用户：只能查看K8S服务器
     */
    public static final String ROLE_ROUTINE_NAME = "routine";

    /**
     * 是否是第一次登录。
     * 0：第一次登录不需要修改密码；1：第一次登录需要修改密码；2：该用户已经完成第一次登录修改密码
     */
    public static final int MODIFY_UNNECESSARY = 0;

    public static final int MODIFY_NECESSARY = 1;

    public static final int MODIFIED_NECESSARY = 2;

    /**
     * 用户类型。1：体验用户；2：正式用户
     */
    public static final int LIMITED_USER = 1;

    public static final int FORMAL_USER = 2;

    /**
     * 用户的意见提交成功
     */
    public static final String COMMENT_CREATED = "提交成功";
}

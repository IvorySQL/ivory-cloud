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
/* ------------------------------------------------ 
 * 
 * 文件名称: UrlConstants.java
 *
 * 摘要： 
 *      URL的一些常量。
 *
 * 作者信息及编写日期：chushaolin@highgo.com，2023-9-27 9:36:44.
 *
 * 修改信息：（如果需要）
 * 2023-9-27，chushaolin@highgo.com，具体修改内容。
 * 
 * 版权信息：
 * Copyright (c) 2009-2099, HighGo Software Co.,Ltd. All rights reserved. 
 *
 *文件路径：
 *		com.highgo.cloud.auth.constant.UrlConstants.java
 *
 *-------------------------------------------------
 */

public class UrlConstants {

    /**
     * 该application  的版本
     */
    public static final String VERSION = "/v1/";

    /**
     * 用户登录的URL
     */
    public static final String LOGIN_URL = "/login";

    /**
     * 用户退出的URL
     */
    public static final String LOGOUT_URL = "/logout";

    /**
     * 前端页面login form 的username key
     */
    public static final String LOGIN_FORM_USERNAME_KEY = "username";

    /**
     * 前端页面login form 的password key
     */
    public static final String LOGIN_FORM_PASSWORD_KEY = "password";

    // /**
    // * admin用户访问的URL
    // */
    // public static final String ADMIN_URL = "/admin";
    //
    // /**
    // * routine用户访问的URL
    // */
    // public static final String ROUTINE_URL = "/routine";

}

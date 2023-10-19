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

package com.highgo.cloud.constant;

/* ------------------------------------------------ 
 * 
 * 文件名称: DBInstallResultMessage.java
 *
 * 摘要： 
 *      数据库安装及结果码。
 *
 * 作者信息及编写日期：chushaolin@highgo.com，2023-6-2 14:32:50.
 *
 * 修改信息：（如果需要）
 * 2023-6-2，chushaolin@highgo.com，具体修改内容。
 * 
 * 版权信息：
 * Copyright (c) 2009-2099, HighGo Software Co.,Ltd. All rights reserved. 
 *
 *文件路径：
 *		com.highgo.cloud.contants.DBInstallResultMessage.java
 *
 *-------------------------------------------------
 */
public class DBInstallResultMessage {

    /**
     * 安装成功
     */
    public static final String SUCCESS = "数据库安装成功";

    /**
     * 安装数据库时，做服务器的准备工作失败
     */
    public static final String PREPARE_SERVER_FAILED = "安装依赖包失败";

    public static final String INSUFFICIENT_SERVER_INFO = "符合条件的服务器数量不足";

    public static final String INSUFFICIENT_EIP = "符合条件的弹性公网IP数量不足";

    public static final String INSUFFICIENT_VIP = "符合条件的虚拟IP数量不足";

    public static final String INSTALL_DB_FAILED = "安装数据库失败";

    public static final String TURN_ON_HGPROXY_FAILED = "开启读写分离失败";

    public static final String NOT_CONFIGUGED_VIP_RANGE = "没有配置虚拟IP段";

    public static final String INTERNAL_ERROR = "内部错误";

    public static final String CREATE_TIMEOUT = "数据库搭建超时";

    public static final String UN_SUPPORTED_CLOUD = "不支持云服务类型！";

}

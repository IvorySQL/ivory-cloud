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

/**
 * DB 操作的常量类
 *
 * @author chushaolin
 */
public class DBConstant {

    /**
     * 停止数据库的操作方法名字
     */
    public static final String STOPDB_NAME = "stop";

    /**
     * 停止数据库的操作方法代码
     */
    public static final int STOPDB_VALUE = 1;

    /**
     * 重启数据库的操作方法名字
     */
    public static final String RESTARTDB_NAME = "restart";

    /**
     * 高可用  主备切换指令
     */
    public static final String DBHA_SWITCHOVER = "switchover";

    /**
     * 重启数据库的操作方法代码
     */
    public static final int RESTARTDB_VALUE = 2;

    /**
     * 启动数据库的操作方法名字
     */
    public static final String STARTDB_NAME = "start";

    /**
     * 启动数据库的操作方法代码
     */
    public static final int STARTDB_VALUE = 3;

    /**
     * 数据库用户名的分隔符
     * 如安全版：sysdba|syssso|syssao
     */
    public static final String DBUSERNAME_SEPERATOR = "\\|";

    /**
     * 纯物理机
     */
    public static final int PHYSICAL_MACHINE = 1;
    /**
     * 华为云
     */
    public static final int PHYSICAL_HUAWEI = 2;

    /**
     * 华为云
     */
    public static final int DOCKER_HUAWEI = 2;

    /**
     * 企业版
     */
    public static final int HIGHGO_ENTER_DB = 1;

    /**
     * 安全版
     */
    public static final int HIGHGO_SEE_DB = 2;

    /**
     * 原生PG
     */
    public static final int PG_DB = 3;

    /**
     * 企业版 安装包名
     */
    public static final String INSTALL_ENTER_DB_PACKAGE = "enter";

    /**
     * 安全版 安装包名
     */
    public static final String INSTALL_SEE_DB_PACKAGE = "see";

    /**
     * BMS所有的安装包名
     */
    public static final String INSTALL_PACKAGE_NAME = "installPkgs";

    /**
     * ha  主节点
     */
    public static final Integer PRIMARY = 0;

    /**
     * ha  备节点
     */
    public static final Integer STANDBY = 1;

    /**
     * ha 主
     */
    public static final String PRIMARY_STR = "primary";

    /**
     * ha  备
     */
    public static final String STANDBY_STR = "standby";

    /**
     * 数据库默认data 文件夹名
     */
    public static final String DEFAULT_DATA_DIR = "data";

    /**
     * pg 原版数据库，add to hgadmin, its db's username
     */
    public static final String POSTGRES = "postgres";

    /**
     * root
     */
    public static final String ROOT = "root";

    /**
     * 初始化服务器需要安装的依赖的文件夹名
     */
    public static final String INSTALL_DEPENDENCIES_PACKAGE = "localDependencies";

    /**
     * pg 默认端口号
     */
    public static final int PG_DEFAULT_PORT = 5432;

    /**
     * 安全版 默认端口号
     */
    public static final int SEE_DEFAULT_PORT = 5866;

    /**
     * 企业版 默认端口号
     */
    public static final int ENTER_DEFAULT_PORT = 5432;

    /**
     *数据库内核版本
     */
    public static final int PG_KERNEL_VERSION = 12;

    /**
     *数据库内核版本
     */
    public static final int IVORY_PG_KERNEL_VERSION = 15;

    /**
     * 数据库管理员 sysdba
     */
    public static final String SYSDBA = "sysdba";

    /**
     * 数据库安全员 syssso
     */
    public static final String SYSSSO = "syssso";

    /**
     * 数据库审计员 syssao
     */
    public static final String SYSSAO = "syssao";

    /**
     * 数据库  highgo
     */
    public static final String HIGHGO = "highgo";

    /**
     * 企业版数据库 highgopg
     */
    public static final String HIGHGOPG = "highgopg";

}

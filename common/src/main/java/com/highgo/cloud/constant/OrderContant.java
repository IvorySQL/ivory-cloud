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

public class OrderContant {

    // 数据库已经被删除
    public static final String DB_DIED = "DELETED";

    /**
     * cpu架构x86
     */
    public static final int CPU_TYPE_X86 = 1;

    /**
     * cpu架构arm
     */
    public static final int CPU_TYPE_ARM = 2;

    /**
     * cpu架构x86  安装包名
     */
    public static final String X86_PACKAGE = "x86";

    /**
     * cpu架构arm  安装包名
     */
    public static final String ARM_PACKAGE = "arm";

    // must be same with server_id in table server
    /**
     * 虚拟机
     */
    public static final int SERVER_VM = 1;
    public static final String SERVER_VM_NAME = "虚拟机";

    /**
     * 裸金属（类云）
     */
    public static final int SERVER_PHYSICAL = 2;
    public static final String SERVER_PHYSICAL_NAME = "裸金属";
    /**
     * 容器
     */
    public static final int SERVER_CONTAINER = 3;
    public static final String SERVER_CONTAINER_NAME = "容器";

    /**
     * 记录未被删除
     */
    public static final int RECORD_IS_NOT_DELETE = 0;

    /**
     * 记录已被删除
     */
    public static final int RECORD_IS_DELETE = 1;

    /**
     * 记录不允许共享
     */
    public static final int RECORD_NOT_SHARE = 0;

    /**
     * 记录允许共享
     */
    public static final int RECORD_SHARE = 1;

    /**
     * 源地址：所有地址均可
     */
    public static final String FULL_CIDR = "0.0.0.0/0";

    /**
     * 删除记录成功
     */
    public static final String DEL_SUCCESS = "success";

    /**
     * 删除记录失败
     */
    public static final String DEL_FAILED = "fail";

    /**
     * 删除DB脚本的路径
     */
    public static final String DELETE_DB_DIR = "deleteDB";

    /**
     * 修改数据库密码脚本的路径
     */
    public static final String CHANGE_DB_PWD_DIR = "changeDbPwd";

    /**
     * 获取pdr脚本的路径
     */
    public static final String HGPROXY_DIR = "hgproxy";

    /**
     * 操作数据库脚本的路径
     */
    public static final String DB_OPERATION_DIR = "dbOperation";

    /**
     * 数据库数据目录
     */
    public static final String DB_DATA_DIR = "Highgo";

    /**
     * 未使用
     */
    public static final int UN_USED = 0;

    /**
     * 已使用
     */
    public static final int IN_USED = 1;

    /**
     * 未安装数据库，
     */
    public static final int UN_INSTALL_DB = 0;

    /**
     * 已经安装数据库
     */
    public static final int INSTALLED_DB = 1;

    /**
     * 服务器上没有上传初始化环境的包
     */
    public static final int BMS_NOT_INIT_PACKAGE = 2;

    /**
     * 安装数据库失败
     */
    public static final int INSTALL_DB_FAILED = -1;

    /**
     * table charging_mode, cm_type值对应
     * 按需
     */
    public static final int CHARGINGMODE_POST = 1;

    /**
     * table charging_mode, cm_type值对应
     * 包年约/月
     */
    public static final int CHARGINGMODE_PRE = 2;

    /**
     * 服务提供商 纯物理机
     * 与table provider_type的字段type 对应
     */
    public static final int PROVIDER_PURE_BMS_SERVER = 1;

    public static final String PROVIDER_CODE_PHYSICAL = "physical";
    /**
     * 服务提供商 华为云
     * 与table provider_type的字段type 对应
     */
    public static final int PROVIDER_HUAWEI = 2;
    public static final String PROVIDER_CODE_HUAWEI = "huawei";

    /**
     * 服务提供商 阿里云
     * 与table provider_type的字段type 对应
     */
    public static final int PROVIDER_ALIYUN = 3;
    public static final String PROVIDER_CODE_ALIYUN = "ali";

    /**
     * 服务提供商 浪潮云
     * 与table provider_type的字段type 对应
     */
    public static final int PROVIDER_LANGCHAO = 4;
    public static final String PROVIDER_CODE_LANGCHAO = "langchao";

    /**
     * 服务提供商 天翼云
     * 与table provider_type的字段type 对应
     */
    public static final int PROVIDER_TIANYI = 5;
    public static final String PROVIDER_CODE_TIANYI = "tianyi";

    /**
     * 服务提供商 信服云
     * 与table provider_type的字段type 对应
     */
    public static final int PROVIDER_XINFU = 6;
    public static final String PROVIDER_CODE_XINFU = "xinfu";

    /**
     * 开启 bms  配额
     */
    public static final int OPEN_BMS_QUOTA = 1;

    /**
     * 关闭 bms  配额
     */
    public static final int CLOSE_BMS_QUOTA = 0;

}

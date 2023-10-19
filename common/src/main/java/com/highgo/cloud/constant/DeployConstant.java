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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author: highgo-lucunqiao
 * @date: 2023/6/25 15:19
 * @Description: 数据库部署相关常量
 */
public class DeployConstant {

    // must be same with db_conf_id in table db_configuration
    /**
     * 单实例
     * 数据库独享服务器、实例。
     */
    public static final int EXCLUSIVE_INSTANCE = 1;

    /**
     * 一个服务器多个实例
     */
    public static final int MUL_INSTANCES = 2;

    /**
     * 一个实例多个数据库
     */
    public static final int SHARED_INSTANCE = 3;

    public static final String SHARED_INSTANCE_NAME = "sharedHighgo";

    public static final int SHARED_DISK = 500;

    public static final int SHARED_BANDWIDTH = 100;
    public static final String IPTYPESUFFIX = "_sbgp";

    // must be same with deploy_mode in table server
    /**
     * 未部署
     */
    public static final int NOT_DEPLOY_DB = 0;

    /**
     * 单机
     */
    public static final int DEPLOY_BASE = 1;
    public static final String DEPLOY_BASE_NAME = "单机";
    /**
     * dbha高可用
     */
    public static final int DEPLOY_DBHA = 2;
    public static final String DEPLOY_CLUSTER_NAME = "高可用";
    /**
     * 分布式
     */
    public static final int DEPLOY_DISTRIBUTION = 3;
    public static final String DEPLOY_DISTRIBUTION_NAME = "分布式";

    /**
     * 对等服务
     */
    public static final int DEPLOY_PEERSERVICE = 4;
    public static final String DEPLOY_PEERSERVICE_NAME = "对等服务";

    /**
     * hghac高可用
     */
    public static final int DEPLOY_HGHAC = 5;
    public static final String DEPLOY_HGHAC_NAME = "HGHAC高可用";
    /**
     * hghac 读写分离
     */
    public static final int DEPLOY_HGPROXY_HGHAC = 6;
    // public static final String DEPLOY_HGPROXY_NAME = "HGPROXY";

    /**
     * dbha 读写分离
     */
    public static final int DEPLOY_HGPROXY_DBHA = 7;

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
     * dbha部署类型
     */
    public static final List<Integer> DBHA_DEPLOY = new ArrayList<>(Arrays.asList(
            DEPLOY_DBHA,
            DEPLOY_HGPROXY_DBHA));
    /**
     * hghac部署类型
     */
    public static final List<Integer> HGHAC_DEPLOY = new ArrayList<>(Arrays.asList(
            DEPLOY_HGHAC,
            DEPLOY_HGPROXY_HGHAC));
}

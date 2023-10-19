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
 * @author: highgo-lucunqiao
 * @date: 2023/6/25 14:16
 * @Description: for saas
 */
public class SaasConstant {

    /**
     * SAAS 默认的数据库密码
     */
    public static final String SAAS_DB_DEFAULT_PASSWORD = "highgo";

    /**
     * 数据库类型：标准版
     */
    public static final int SAAS_POSTGRESQL_TYPE = 0;

    /**
     * 默认一次订阅一个数据库
     */
    public static final int SAAS_DB_COUNT = 1;

    /**
     * 默认磁盘大小
     */
    public static final int SAAS_DEFAULT_DISK_SIZE = 60;

    // 数据库冻结
    public static final String DB_FROZEN = "frozen";
}

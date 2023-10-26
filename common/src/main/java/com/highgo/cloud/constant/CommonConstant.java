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

import java.util.concurrent.TimeUnit;

/**
 * @author
 * @date 2019/2/1
 */
public class CommonConstant {

    /**
     * 子网掩码
     */
    public static final String DEFAULT_NET_MASK = "24";

    /**
     * DES加密算法的 key
     */
    public static final String AES_KEY = "hiiagwe2452";

    /**
     * 拷贝到服务器脚本使用的ip
     * 1:公网
     */
    public static final int SCP_PUBLIC_IP = 1;

    /**
     * 拷贝到服务器脚本使用的ip
     * 2:内网
     */
    public static final int SCP_PRIVATE_IP = 2;

    /**
     * URL  前缀
     */
    public static final String HTTP_PREFIX = "http://";

    public static final int DEFAULT_SSH_PORT = 22;

    /**
     * 返回码：错误
     */
    public static final int FAILED = -1;

    /**
     * 返回码：正确
     */
    public static final int SUCCESS = 0;

    /**
     * 执行脚本返回成功
     */
    public static final boolean RUN_SCRIPT_TRUE = true;

    /**
     * 执行脚本返回失败
     */
    public static final boolean RUN_SCRIPT_FALSE = false;

    /**
     * 编码格式
     */
    public static final String ENCODED_UTF8 = "UTF-8";

    /**
     * 默认为空消息
     */
    public static final String DEFAULT_NULL_MESSAGE = "暂无承载数据";
    /**
     * 默认成功消息
     */
    public static final String DEFAULT_SUCCESS_MESSAGE = "操作成功";
    /**
     * 默认失败消息
     */
    public static final String DEFAULT_FAILURE_MESSAGE = "操作失败";

    /**
     *  PG_RMAN初始化配置文件
     *
     */
    public static final String PG_RMAN_CONFIG_FILE_NAME = "db_backup.ini";

    /**
     * linux定时任务文件
     */
    public static final String OS_TIMER_CRONTAB_FILE = "/etc/crontab";

    /**
     *  删除的记录
     */
    public static final int DELETED = 1;
    public static final int UNDELETED = 0;

    /**
     * 毫秒
     */
    public static final int MILLISECONDS = 1000;

    /**
     * 脚本执行超时后，kill 脚本
     */
    public static final boolean KILL_SCRIPT_AFTER_TIMEOUT = true;

    /**
     * 脚本执行超时后，no kill 脚本
     */
    public static final boolean NO_KILL_SCRIPT_AFTER_TIMEOUT = false;

    /**
     * default time unit
     */
    public static final TimeUnit DEFAULT_TIMEUNIT = TimeUnit.SECONDS;

}

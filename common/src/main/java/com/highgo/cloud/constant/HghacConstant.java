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

public class HghacConstant {

    /**
     * 节点类型：PRIMARY（主节点）
     */
    public static final String NODETYPE_LEADER = "leader";

    /**
     * 节点类型：STANDBY（备节点）,X表示数字
     */
    public static final String NODETYPE_REPLICA = "replica";

    /**
     * 监听rest api的端口
     */
    public static final String REST_API_LISTEN_PORT = "8008";

    /**
     *  暂停集群
     */
    public static final String PAUSE_CLUSTER_CMD =
            "/usr/local/hghac/hac/hghactl/hghactl -c /usr/local/hghac/hac/hghac.yml pause --wait";

    /**
     *  恢复集群
     */
    public static final String RESUME_CLUSTER_CMD =
            "/usr/local/hghac/hac/hghactl/hghactl -c /usr/local/hghac/hac/hghac.yml resume --wait";

    /**
     *  启停hghac服务
     */
    public static final String STOP_HGHAC_SERVICE = "systemctl stop hghac";
    public static final String START_HGHAC_SERVICE = "systemctl start hghac";
    public static final String RESTART_HGHAC_SERVICE = "systemctl restart hghac";

}

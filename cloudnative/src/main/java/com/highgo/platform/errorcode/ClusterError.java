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

package com.highgo.platform.errorcode;

/**
 * @author: highgo-lucunqiao
 * @date: 2023/3/3 10:23
 * @Description: k8s集群相关错误代码
 */
public enum ClusterError implements BaseError {

    CLUSTER_NOT_EXIST_ERROR("200.004001", "cluster.not.exist"), // 集群不存在
    CLUSTER_MASTER_IP_CONFLICT_ERROR("200.004002", "cluster.create.master.ip.conflict"), // 集群master ip冲突
    CLUSTER_CONFIG_ERROR("200.004003", "cluster.get.config.failed"), // 获取集群 config 失败
    CLUSTER_NOT_ALLOW_ERROR("200.004004", "cluster.has.instance.do.not.allow"), // 集群不允许删除、更新
    CLUSTER_MASTER_IP_IN_PROCESS("200.004005", "cluster.master.ip.inprocess"), // 集群master ip正在处理中

    CLUSTER_MASTER_IP_UNREACHABLE("200.004006", "cluster.master.ip.unreachable"), // 集群master ip不可达到
    CLUSTER_CONFIG_INVALID("200.004007", "cluster.config.invalid");// 集群master的config文件无效

    private String code;
    private String message;

    ClusterError(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message(Object... args) {
        return this.message;
    }

    @Override
    public String message() {
        return this.message;
    }
}

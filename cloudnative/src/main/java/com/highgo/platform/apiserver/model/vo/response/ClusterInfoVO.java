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

package com.highgo.platform.apiserver.model.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author lucunqiao
 * @date 2023/2/8
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClusterInfoVO {

    /**
     * k8s 集群信息表
     */

    private String clusterId; // 集群id

    private String serverUrl; // 集群ip

    private String serverUser; // 集群user

    private String serverPass; // 集群password

    private String serverSshport; // 集群ssh port

    private String config; // 集群配置信息

    private String clusterName; // 集群名称

    private Date createdAt; // 集群创建时间

    private Date updatedAt; // 集群更新时间

    private String configPath; // config文件路径

}

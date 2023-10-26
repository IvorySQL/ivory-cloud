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

package com.highgo.platform.apiserver.model.po;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @description k8s 集群信息表
 * @author srk
 * @date 2023/9/21 10:08
 */

@Entity
@Data
@Table(name = "k8s_cluster_info")
public class K8sClusterInfoPO extends BaseEntity {

    /**
     *  集群id
     */
    private String clusterId;
    /**
     *  集群ip
     */
    private String serverUrl;
    /**
     *  集群配置信息
     */
    private String config;
    /**
     *  集群名称
     */
    private String clusterName;
    /**
     *  ssh  user
     */
    private String serverUser;
    /**
     *  ssh pass
     */
    private String serverPass;
    /**
     *  ssh port
     */
    private int serverSshport;
    /**
     *  config文件路径
     */
    private String configPath;
}

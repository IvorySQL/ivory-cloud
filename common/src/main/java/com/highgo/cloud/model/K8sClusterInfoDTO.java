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

package com.highgo.cloud.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class K8sClusterInfoDTO implements Serializable {

    private String clusterId;
    private String clusterName;
    private String serverUrl;
    private String config;

    private String serverUser; // ssh user

    private String serverPass; // ssh pass

    private Integer serverSshport; // ssh port

    private String configPath; // config文件路径

    private Date createdAt;

    private Date updatedAt;

    private Date deletedAt;

    private Boolean isDeleted = false;

}

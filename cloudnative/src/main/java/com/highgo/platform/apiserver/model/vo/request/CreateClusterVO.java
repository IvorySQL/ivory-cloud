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

package com.highgo.platform.apiserver.model.vo.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author lucunqiao
 * @date 2023/2/8
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateClusterVO implements Serializable {

    /**
     * 创建 k8s 集群信息
     */
    @NotBlank
    private String serverUrl; // 集群ip

    private String clusterName; // 集群名称

    private String serverUser; // ssh user

    private String serverPass; // ssh pass

    private Integer serverSshport; // ssh port

    private String configPath; // config文件路径

    private String clusterId; // 集群id

}

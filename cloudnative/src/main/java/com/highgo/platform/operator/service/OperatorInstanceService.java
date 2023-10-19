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

package com.highgo.platform.operator.service;

import com.highgo.platform.operator.cr.DatabaseCluster;
import com.highgo.platform.operator.cr.bean.instance.Instance;

public interface OperatorInstanceService {

    /**
     * 构建cr instance节点
     * @param name 实例名称
     * @param replicas 实例副本数
     * @param cpu cpu配置
     * @param memory 内存配置
     * @param storage 磁盘配置
     * @param storageClass 存储类型
     * @return
     */
    public Instance geInstance(String name, int replicas, int cpu, int memory, String storage, String storageClass);

    /**
     * 获取cr instance 节点数量
     * @param databaseCluster
     * @return
     */
    public Integer getNodeNum(DatabaseCluster databaseCluster);

    /**
     * 获取cr instance ready节点数量
     * @param databaseCluster
     * @return
     */
    public Integer getNodeReadyNum(DatabaseCluster databaseCluster);

    boolean isAllPodRebuild(String clusterId, String namespace, String crName, String instanceId);
}

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

package com.highgo.platform.apiserver.repository;

import com.highgo.platform.apiserver.model.po.InstancePO;
import com.highgo.cloud.enums.InstanceStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface InstanceRepository extends BaseRepository<InstancePO, String> {

    @Override
    @Query("select i from InstancePO i where i.isDeleted = false and i.id = ?1")
    Optional<InstancePO> findById(String instanceId);

    @Query(value = "select count(i) from InstancePO i where i.isDeleted = false and i.clusterId = ?1 and i.namespace = ?2 and i.name = ?3")
    Integer countByClusterAndNamespaceAndName(String clusterId, String namespace, String name);

    /**
     * 查询指定集群和命名空间的实例列表
     * @param clusterId
     * @param namespaces
     * @return
     */
    @Query(value = "select i from InstancePO i where i.isDeleted = false  and i.clusterId = ?1 and i.namespace in ( ?2 ) order by i.createdAt desc")
    List<InstancePO> listByClusterAndNamespaces(String clusterId, List<String> namespaces);

    /**
     * 查询所有实例
     * @return
     */
    @Query(value = "select i from InstancePO i where i.isDeleted = false order by i.createdAt desc")
    List<InstancePO> listAll();

    /**
     * 查询用户下的实例
     * @return
     */
    @Query(value = "select i from InstancePO i where i.isDeleted = false and i.creator = ?1 order by i.createdAt desc")
    List<InstancePO> listByUserId(String userId);

    /**
     * 查询集群下的实例
     * @return
     */
    @Query(value = "select i from InstancePO i where i.isDeleted = false and i.clusterId = ?1 order by i.createdAt desc")
    List<InstancePO> listByClusterId(String clusterId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update InstancePO i set i.status = ?3 , i.updatedAt = ?2 where i.id = ?1")
    void updateStatusByInstanceId(String instanceId, Date date, InstanceStatus status);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update InstancePO i set i.cpu = ?3 , i.updatedAt = ?2 where i.id = ?1")
    void updateCpuByInstanceId(String instanceId, Date date, Integer cpu);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update InstancePO i set i.memory = ?3 , i.updatedAt = ?2 where i.id = ?1")
    void updateMemoryByInstanceId(String instanceId, Date date, Integer memory);

    /**
     * 查询集群下的实例/userId数据隔离
     * @return
     */
    @Query(value = "select i from InstancePO i where i.isDeleted = false and i.clusterId = ?1 and i.creator = ?2 order by i.createdAt desc")
    List<InstancePO> listByClusterIdUserId(String clusterId, String userId);

}

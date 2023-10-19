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

import com.highgo.platform.apiserver.model.po.K8sClusterInfoPO;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface K8sClusterInfoRepository extends BaseRepository<K8sClusterInfoPO, String> {

    @Query(value = "select k from K8sClusterInfoPO k where k.clusterId = ?1 and k.isDeleted=false")
    Optional<K8sClusterInfoPO> findByClusterId(String clusterId);

    @Query(value = "select k.clusterId from K8sClusterInfoPO k where k.isDeleted=false ")
    List<String> listClusterId();

    @Query(value = "select k from K8sClusterInfoPO k where k.isDeleted=false ")
    List<K8sClusterInfoPO> listCluster();

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update K8sClusterInfoPO k set k.isDeleted = true, k.deletedAt = ?2 where k.clusterId = ?1")
    void deleteByClusterId(String id, Date date);
}

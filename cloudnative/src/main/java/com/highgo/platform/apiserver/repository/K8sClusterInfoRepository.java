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

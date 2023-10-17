package com.highgo.platform.apiserver.repository;

import com.highgo.platform.apiserver.model.po.AutoScalingSwitchPO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AutoScalingSwitchRepository extends BaseRepository<AutoScalingSwitchPO, String> {

    @Override
    @Query("select a from AutoScalingSwitchPO a where a.isDeleted = false")
    List<AutoScalingSwitchPO> findAll();

    AutoScalingSwitchPO findByUserIdAndClusterIdAndIsDeleted(String userId, String clusterId, Boolean isDeleted);

}

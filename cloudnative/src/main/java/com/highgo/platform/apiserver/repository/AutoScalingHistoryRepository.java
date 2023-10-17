package com.highgo.platform.apiserver.repository;

import com.highgo.platform.apiserver.model.po.AutoScalingHistoryPO;
import com.highgo.cloud.enums.AutoScalingOperation;
import com.highgo.cloud.enums.AutoScalingStatus;
import com.highgo.cloud.enums.AutoScalingType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AutoScalingHistoryRepository extends BaseRepository<AutoScalingHistoryPO, String> {

    @Override
    @Query("select a from AutoScalingHistoryPO a where a.isDeleted = false and a.id = ?1")
    Optional<AutoScalingHistoryPO> findById(String autoScalingHistoryId);

    List<AutoScalingHistoryPO> findByInstanceIdAndStatusOrderByCreatedAtDesc(String instanceId, AutoScalingStatus status);
    @Query("select a from AutoScalingHistoryPO a where a.isDeleted = false and a.instanceId = ?1 and a.type = ?2 and a.operation = ?3 order by a.createdAt desc")
    List<AutoScalingHistoryPO> findByInstanceIdAndSameAutoScaling(String instanceId, AutoScalingType type, AutoScalingOperation operation, Pageable pageable);

}

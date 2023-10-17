package com.highgo.platform.apiserver.repository;

import com.highgo.platform.apiserver.model.po.AutoScalingAlertRulePO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AutoScalingAlertRuleRepository extends BaseRepository<AutoScalingAlertRulePO, String> {

    @Override
    @Query("select a from AutoScalingAlertRulePO a where a.isDeleted = false and a.id = ?1")
    Optional<AutoScalingAlertRulePO> findById(String autoScalingAlertRuleId);

    List<AutoScalingAlertRulePO> findByClusterIdAndUserIdAndIsDeleted(String cluserId, String userId, Boolean isDeleted);

}

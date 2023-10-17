package com.highgo.platform.apiserver.repository;

import com.highgo.platform.apiserver.model.po.BackupPolicyPO;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Repository
public interface BackupPolicyRepository extends BaseRepository<BackupPolicyPO, String> {


    @Query(value = "select b from BackupPolicyPO b where b.instanceId = ?1")
    Optional<BackupPolicyPO> findByInstanceId(String instanceId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update BackupPolicyPO b  set b.isDeleted = true, b.deletedAt = ?2 where b.instanceId = ?1 ")
    void deleteByInstanceId(String instanceId, Date date);
}

package com.highgo.platform.apiserver.repository;

import com.highgo.platform.apiserver.model.po.ConfigChangeHistoryPO;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Repository
public interface ConfigChangeHistoryRepository extends BaseRepository<ConfigChangeHistoryPO, String> {

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query("update ConfigChangeHistoryPO c set c.isDeleted = true, c.deletedAt = ?2 where c.instanceId = ?1")
    void deleteByInstanceId(String id, Date date);
}

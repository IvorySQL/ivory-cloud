package com.highgo.platform.apiserver.repository;

import com.highgo.platform.apiserver.model.po.ConfigInstanceParamPO;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
public interface ConfigInstanceParamRepository extends BaseRepository<ConfigInstanceParamPO, String> {

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query("update ConfigInstanceParamPO c set c.isDeleted = true, c.deletedAt=?2 where c.instanceId = ?1")
    void deleteByInstanceId(String id, Date date);

    @Query("select c from ConfigInstanceParamPO c where c.isDeleted = false and c.instanceId = ?1")
    List<ConfigInstanceParamPO> listByInstanceId(String instanceId);
}

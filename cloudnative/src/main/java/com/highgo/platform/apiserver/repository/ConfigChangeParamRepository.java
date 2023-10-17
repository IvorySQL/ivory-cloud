package com.highgo.platform.apiserver.repository;

import com.highgo.platform.apiserver.model.po.ConfigChangeParamPO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfigChangeParamRepository extends BaseRepository<ConfigChangeParamPO, String> {

    @Query(value = "select c from ConfigChangeParamPO c where c.isDeleted = false and c.configChangeHistoryId = ?1")
    List<ConfigChangeParamPO> listConfigChangeParamByHistoryId(String historyId);
}

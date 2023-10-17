package com.highgo.platform.apiserver.repository;

import com.highgo.platform.apiserver.model.po.ConfigParamDefinationPO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ConfigDefinationRepository extends BaseRepository<ConfigParamDefinationPO, String> {

    @Query(value = "select p.name as name, p.paramType as paramType, p.defaultValue as defaultValue, c.value as runningValue, p.min as min, p.max as max, p.enumValue as enumValue, p.rule as rule, p.description as description from ConfigParamDefinationPO p LEFT JOIN ConfigInstanceParamPO c on p.name = c.name where c.instanceId = ?1 and c.isDeleted = false and p.isDeleted = false ORDER BY p.name ASC")
    List<Map> listParamByInstanceId(String instanceId);
}

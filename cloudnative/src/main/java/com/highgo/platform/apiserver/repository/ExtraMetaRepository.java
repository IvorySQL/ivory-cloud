package com.highgo.platform.apiserver.repository;

import com.highgo.platform.apiserver.model.po.ExtraMetaPO;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExtraMetaRepository extends BaseRepository<ExtraMetaPO, String> {

    @Query(value = "select t from ExtraMetaPO t where t.instanceId = ?1 and t.name = ?2 and t.isDeleted = false")
    Optional<ExtraMetaPO> findByInstanceIdAndName(String instanceId, String name);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = " update ExtraMetaPO e set e.deletedAt = ?2, e.isDeleted = true where e.instanceId = ?1")
    void deleteByInstanceId(String instanceId, Date date);

    @Query(value = "select t from ExtraMetaPO t where t.instanceId = ?1 and t.isDeleted = false")
    List<ExtraMetaPO> findByInstanceId(String instanceId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = " update ExtraMetaPO e set e.updatedAt = ?4, e.value = ?3 where e.instanceId = ?1 and e.name = ?2")
    void updateValueByNameAndInstanceId(String instanceId, String name, String value, Date date);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "delete from ExtraMetaPO e where e.instanceId =?1 and e.name = ?2")
    void deleteByInstanceIdAndName(String instanceId, String name);
}

package com.highgo.platform.apiserver.repository;

import com.highgo.platform.apiserver.model.po.RestoreRecordPO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author srk
 * @version 1.0
 * @project micro_service_cloud
 * @description 数据库恢复记录表操作类
 * @date 2023/9/25 17:33:09
 */
@Repository
public interface RestoreRecordRepository extends BaseRepository<RestoreRecordPO, String>{

	/**
	 * @description 根据实例id查询数据库恢复记录
	 *
	 * @param: instanceId
	 * @return Optional<RestoreRecordPO>
	 * @author srk
	 * @date 2023/9/25 17:36
	 */

	@Query("select i from RestoreRecordPO i where i.isDeleted = false and i.instanceId = ?1")
	Optional<RestoreRecordPO> findRestoreRecordPOByInstanceId(String instanceId);
}

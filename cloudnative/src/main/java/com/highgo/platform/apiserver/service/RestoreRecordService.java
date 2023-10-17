package com.highgo.platform.apiserver.service;

import com.highgo.platform.apiserver.model.dto.RestoreRecordDTO;

/**
 * @author srk
 * @version 1.0
 * @project micro_service_cloud
 * @description 数据库恢复记录操作service
 * @date 2023/9/25 17:37:36
 */
public interface RestoreRecordService {
	RestoreRecordDTO getRestoreRecordByInstanceId(String instanceId);

	void createOrModifyRestoreRecord(RestoreRecordDTO restoreRecordDTO);

}

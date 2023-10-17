package com.highgo.platform.apiserver.service.impl;

import com.highgo.cloud.util.BeanUtil;
import com.highgo.cloud.util.CommonUtil;
import com.highgo.platform.apiserver.model.dto.RestoreRecordDTO;
import com.highgo.platform.apiserver.model.po.RestoreRecordPO;
import com.highgo.platform.apiserver.repository.RestoreRecordRepository;
import com.highgo.platform.apiserver.service.RestoreRecordService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * @author srk
 * @version 1.0
 * @project micro_service_cloud
 * @description 数据库恢复记录操作实现类
 * @date 2023/9/25 17:39:12
 */
@Service
public class RestoreRecordServiceImpl implements RestoreRecordService {
	@Resource
	private RestoreRecordRepository restoreRecordRepository;

	@Override
	public RestoreRecordDTO getRestoreRecordByInstanceId(String instanceId) {
		Optional<RestoreRecordPO> restoreRecord = restoreRecordRepository.findRestoreRecordPOByInstanceId(instanceId);
		if (restoreRecord.isPresent()) {
			RestoreRecordDTO dto = new RestoreRecordDTO();
			BeanUtil.copyNotNullProperties(restoreRecord.get(), dto);
			return dto;
		}
		return null;
	}

	@Override
	public void createOrModifyRestoreRecord(RestoreRecordDTO restoreRecordDTO) {
		if (restoreRecordDTO == null) {
			return;
		}
		RestoreRecordPO po = new RestoreRecordPO();
		BeanUtil.copyNotNullProperties(restoreRecordDTO, po);
		restoreRecordRepository.save(po);
	}

}

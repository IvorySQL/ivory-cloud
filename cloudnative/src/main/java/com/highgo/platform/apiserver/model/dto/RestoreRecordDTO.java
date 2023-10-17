package com.highgo.platform.apiserver.model.dto;

import lombok.Data;

/**
 * @author srk
 * @version 1.0
 * @project micro_service_cloud
 * @description 数据库恢复记录
 * @date 2023/9/25 17:30:19
 */
@Data
public class RestoreRecordDTO {
	/**
	 *  id
	 */
	private String id;

	/**
	 *  数据库实例id
	 */
	private String instanceId;
	/**
	 *  恢复开始时间
	 */
	private String startTime;
	/**
	 *  恢复结束时间
	 */
	private String completionTime;
	/**
	 *  是否恢复完成
	 */
	private Boolean finished;
}


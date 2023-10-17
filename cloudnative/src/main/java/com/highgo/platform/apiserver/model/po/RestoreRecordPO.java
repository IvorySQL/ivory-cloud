package com.highgo.platform.apiserver.model.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author srk
 * @version 1.0
 * @project micro_service_cloud
 * @description 数据库恢复记录表
 * @date 2023/9/25 17:26:24
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "restore_record")
@Builder
public class RestoreRecordPO extends BaseEntity{

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

package com.highgo.cloud.auth.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author srk
 * @version 1.0
 * @project micro_service_cloud
 * @description 用户监控相关信息
 * @date 2023/10/9 17:25:11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonitorUserDto {
	//用户ID
	private int id;

	//用户名
	private String name;
	/**
	 * 命名空间
	 */
	private String namespace;
	/**
	 * 监控状态
	 */
	private String monitorStatus;
	/**
	 * 监控url
	 */
	private String monitorUrl;
	/**
	 * accessMode
	 */
	private String accessMode;
	/**
	 * cluster id
	 */
	private String clusterId;
	/**
	 * 创建时间
	 */
	private Date createdAt;
	/**
	 * 更新时间
	 */
	private Date updatedAt;

}

package com.highgo.cloud.auth.model.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author srk
 * @version 1.0
 * @project micro_service_cloud
 * @description User用户信息
 * @date 2023/9/27 09:48:22
 */
@Data
public class UserVO {
	/**
	 * user信息 创建监控
	 */

	/**
	 * userId
	 */
	private int id;

	/**
	 * 用户名
	 */
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

package com.highgo.cloud.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.highgo.cloud.auth.entity.UserType;

/* ------------------------------------------------ 
 * 
 * 文件名称: UserTypeRepository.java
 *
 * 摘要： 
 *      用户类型操作类。
 *
 * 作者信息及编写日期：chushaolin@highgo.com，2023-9-27 14:55:13.
 *
 * 修改信息：（如果需要）
 * 2023-9-27，chushaolin@highgo.com，具体修改内容。
 * 
 * 版权信息：
 * Copyright (c) 2009-2099, HighGo Software Co.,Ltd. All rights reserved. 
 *
 *文件路径：
 *		com.highgo.cloud.auth.repository.UserTypeRepository.java
 *
 *-------------------------------------------------
 */
@Repository("userTypeRepository")
public interface UserTypeRepository extends JpaRepository<UserType, Integer>{

}


package com.highgo.cloud.auth.model.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/* ------------------------------------------------ 
 * 
 * 文件名称: RegisterUserDTO.java
 *
 * 摘要： 
 *      注册用户信息。
 *
 * 作者信息及编写日期：chushaolin@highgo.com，2023-9-27 9:21:04.
 *
 * 修改信息：（如果需要）
 * 2023-9-27，chushaolin@highgo.com，具体修改内容。
 * 
 * 版权信息：
 * Copyright (c) 2009-2099, HighGo Software Co.,Ltd. All rights reserved. 
 *
 *文件路径：
 *		com.highgo.cloud.auth.model.dto.RegisterUserDTO.java
 *
 *-------------------------------------------------
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterUserDTO {

	/**
	 * 用户名
	 */
    private String name;


    /**
     * 密码
     */
    private String password;

    /**
     * 邮箱
     */
    @Email
    @Pattern(regexp = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$",message = "邮箱不符合格式")
    private String email;

    /**
     * 电话
     */
    private String tel;
    

    /**
     * 1:  体验用户
     * 2: 普通用户
     */
    private int type;

    /**
     * 验证码
     */
    private String verificationCode;
}


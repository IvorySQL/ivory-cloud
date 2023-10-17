package com.highgo.cloud.auth.model.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/* ------------------------------------------------ 
 * 
 * 文件名称: UpdateUserDTO.java
 *
 * 摘要： 
 *      修改用户的信息
 *
 * 作者信息及编写日期：chushaolin@highgo.com，2023-9-27 9:19:53.
 *
 * 修改信息：（如果需要）
 * 2023-9-27，chushaolin@highgo.com，具体修改内容。
 * 
 * 版权信息：
 * Copyright (c) 2009-2099, HighGo Software Co.,Ltd. All rights reserved. 
 *
 *文件路径：
 *		com.highgo.cloud.auth.model.dto.UpdateUserDTO.java
 *
 *-------------------------------------------------
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserDTO {
    //用户名
    private String name;

    //密码
    private String password;


    //邮箱
    @Email
    @Pattern(regexp = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$",message = "邮箱不符合格式")
    private String email;

    /**
     * 验证码
     */
    private String verificationCode;
}


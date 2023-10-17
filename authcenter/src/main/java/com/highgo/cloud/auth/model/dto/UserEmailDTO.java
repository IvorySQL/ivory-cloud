package com.highgo.cloud.auth.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

/* ------------------------------------------------ 
 * 
 * 文件名称: UpdateUserDTO1.java
 *
 * 摘要： 
 *      用户邮箱的信息
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
 *		com.highgo.cloud.auth.model.dto.UserEmailDTO.java
 *
 *-------------------------------------------------
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEmailDTO {


    private String userName;

    //邮箱
    @Email
    @Pattern(regexp = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$",message = "邮箱不符合格式")
    private String userEmail;

}

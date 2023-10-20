/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    // 邮箱
    @Email
    @Pattern(regexp = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$", message = "邮箱不符合格式")
    private String userEmail;

}

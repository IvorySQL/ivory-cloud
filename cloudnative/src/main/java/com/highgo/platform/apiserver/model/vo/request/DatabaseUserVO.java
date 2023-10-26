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

package com.highgo.platform.apiserver.model.vo.request;

import com.highgo.cloud.enums.LockStatus;
import com.highgo.cloud.enums.UserOption;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @author lucunqiao
 * @date 2022/12/14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DatabaseUserVO implements Serializable {

    private String name;

    @NotBlank
    @Pattern(regexp = "^(?=.*[\\!@#\\$%\\^&\\*\\(\\)])(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d\\!@#\\$%\\^&\\*\\(\\)]{8,32}$", message = "{param.instance_password.invalid}")
    @ApiModelProperty(value = "数据库管理员密码", required = true)
    private String password;

    @Enumerated(EnumType.STRING)
    private LockStatus status;

    @Enumerated(EnumType.STRING)
    private UserOption option;
}

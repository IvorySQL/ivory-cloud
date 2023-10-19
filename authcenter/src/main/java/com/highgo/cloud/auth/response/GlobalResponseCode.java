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

package com.highgo.cloud.auth.response;

import org.apache.http.HttpStatus;

public enum GlobalResponseCode implements IErrorCode {

    SUCCESS(HttpStatus.SC_OK, "操作成功！"),

    // 账户相关
    USERNAME_OR_PASSWORD_ERROR(501, "您输入的用户名或密码不正确！"), ACCOUNT_LOCKED_ERROR(502, "账户被锁定！"), CREDENTIALS_EXPIRED_ERROR(
            503, "密码过期！"), ACCOUNT_EXPIRED_ERROR(504, "账户过期！"), ACCOUNT_DISABLED_ERROR(505,
                    "账户被禁用！"), VALIDATE_CODE_NOT_MATCHED_ERROR(506, "验证码不匹配！"), LOGIN_FAILED_ERROR(507,
                            "登录失败！"), USERNAME_NOT_FOUND_ERROR(508,
                                    "用户名不存在！"), UN_AUTHORIZED(HttpStatus.SC_UNAUTHORIZED, "未认证"),
    // 权限
    ACCESS_FORBIDDEN_ERROR(HttpStatus.SC_FORBIDDEN, "无访问权限");

    private int code;
    private String message;

    GlobalResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

}

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

package com.highgo.platform.errorcode;

/**
 * @author: highgo-lucunqiao
 * @date: 2023/4/23 10:59
 * @Description: 自动弹性伸缩错误码
 */
public enum AutoScalingError implements BaseError {

    AUTOSCALINGSWITCH_NOT_EXIST("200.007001", "autoscalingswitch.not.exist"),
    USER_MONITOR_NOT_EXIST("200.007002", "user.monitoring.information.not.exist"),
    CONFIG_ALERTMANAGER_FAILED("200.007003", "config.alertmanager.failed");

    private String code;
    private String message;

    AutoScalingError(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String message(Object... args) {
        return this.message;
    }

    @Override
    public String message() {
        return this.message;
    }

    @Override
    public String code() {
        return code;
    }
}

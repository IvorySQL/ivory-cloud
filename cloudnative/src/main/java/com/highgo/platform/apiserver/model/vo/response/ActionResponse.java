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

package com.highgo.platform.apiserver.model.vo.response;

import com.highgo.cloud.enums.InstanceStatus;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class ActionResponse implements Serializable {

    /**
     * 操作返回值
     */

    private static final long serialVersionUID = 1666078472000L;
    private int code;
    private String message;
    private Boolean success;

    private InstanceStatus status;
    public ActionResponse() {
    }

    public ActionResponse(final int code, final Boolean success) {
        this.code = code;
        this.success = success;
    }

    public ActionResponse(final int code, final Boolean success, final String message) {
        this.code = code;
        this.success = success;
        this.message = message;
    }
    public ActionResponse(final int code, final Boolean success, final String message, final InstanceStatus status) {
        this.code = code;
        this.success = success;
        this.message = message;
        this.status = status;
    }

    public static ActionResponse actionSuccess() {
        return new ActionResponse(200, true);
    }
    public static ActionResponse actionSuccess(String message) {
        return new ActionResponse(200, true, message);
    }

    public static ActionResponse actionFailed() {
        return new ActionResponse(201, false);
    }

    public static ActionResponse actionFailed(String message) {
        return new ActionResponse(201, false, message);
    }

}

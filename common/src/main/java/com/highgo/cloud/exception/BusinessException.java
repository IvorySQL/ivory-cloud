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

package com.highgo.cloud.exception;

import lombok.NoArgsConstructor;

/**
 * BusinessException
 * 业务自定定义运行时异常
 * @author renlizou
 */
@NoArgsConstructor
public class BusinessException extends RuntimeException {
    // private static final long serialVersionUID = 1L;
    //
    // public BusinessException(String message) {
    // super(message);
    // }
    //
    // public BusinessException(Throwable cause) {
    // super(cause);
    // }
    //
    // public BusinessException(String message, Throwable cause) {
    // super(message, cause);
    // }
    //
    // public BusinessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    // {
    // super(message, cause, enableSuppression, writableStackTrace);
    // }

    private static final long serialVersionUID = 1L;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(Throwable cause) {
        super(cause);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}

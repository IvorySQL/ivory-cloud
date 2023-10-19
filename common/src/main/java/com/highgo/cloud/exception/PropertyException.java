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

package com.highgo.cloud.exception; /**
                                    *
                                    */

/**
 * @author cww<br>
 * @version 1.0
 * 2012-12-18 下午1:26:28<br>
 */
public class PropertyException extends RuntimeException {

    private static final String MESSAGE = "org.apache.commons.beanutils.PropertyUtils error!";
    /**
     *
     */
    public PropertyException() {
        super(MESSAGE);
    }

    /**
     * @param message
     */
    public PropertyException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public PropertyException(Throwable cause) {
        super(MESSAGE, cause);
    }

    /**
     * @param message
     * @param cause
     */
    public PropertyException(String message, Throwable cause) {
        super(message, cause);
    }

}
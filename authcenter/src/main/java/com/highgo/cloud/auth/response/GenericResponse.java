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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by pengq on 2019/9/5 18:01.
 */
public abstract class GenericResponse implements RestResponse {

    private boolean isFieldsSet = false;
    private boolean isDataSet = false;
    private Map<String, Object> fields;
    private Object data;
    private int code;
    private String message;

    public GenericResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public GenericResponse appendField(String field, Object value) {
        if (!this.isFieldsSet) {
            this.isFieldsSet = true;
            this.fields = new HashMap<>();
        }

        this.fields.put(field, value);
        return this;
    }

    public GenericResponse withData(Object data) {
        if (!this.isDataSet) {
            this.isDataSet = true;
        }

        this.data = data;
        return this;
    }

    @Override
    public Optional<Map<String, Object>> getFields() {
        return this.isFieldsSet ? Optional.of(this.fields) : Optional.empty();
    }

    @Override
    public Optional<Object> getData() {
        return this.isDataSet ? Optional.ofNullable(this.data) : Optional.empty();
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
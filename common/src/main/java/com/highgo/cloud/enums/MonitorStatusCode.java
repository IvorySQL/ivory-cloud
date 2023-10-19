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

package com.highgo.cloud.enums;

public enum MonitorStatusCode {

    ACTIVE("active", 0), FAILED("failed", 1), BUILDING("building", 2), UNINSTALL("uninstall", 3), ERROR("error", -1);
    private String name;
    private int code;

    MonitorStatusCode(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public static String getName(int code) {
        for (MonitorStatusCode m : MonitorStatusCode.values()) {
            if (m.getCode() == code) {
                return m.name;
            }
        }
        return null;
    }

    // get set 方法
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}

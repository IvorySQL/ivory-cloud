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

import com.highgo.cloud.constant.DBConstant;

/**
 * 数据库操作的枚举类
 * @author chushaolin
 *
 */
public enum DBOperation {

    // 停止，重启
    STOP(DBConstant.STOPDB_VALUE, DBConstant.STOPDB_NAME), RESTART(DBConstant.RESTARTDB_VALUE,
            DBConstant.RESTARTDB_NAME), START(DBConstant.STARTDB_VALUE, DBConstant.STARTDB_NAME);

    // 操作的可读化名字
    private String name;

    // 操作的代码
    private int index;

    private DBOperation(int index, String name) {
        this.name = name;
        this.index = index;
    }

    // 获取代码对应的操作方法名
    public static String getName(int index) {
        for (DBOperation c : DBOperation.values()) {
            if (c.getIndex() == index) {
                return c.name;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

}

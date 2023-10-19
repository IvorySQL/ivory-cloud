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

public enum BackupError implements BaseError {

    DUPLICATE_NAME("200.002001", "instance.backup.duplicate_name"),
    BACKUP_NOT_EXIST("200.002002", "instance.backup.not_exist"),
    BACKUP_FAILED("200.002003", "instance.backup.failed"),
    BACKUP_NOT_ALLOW_OPERATE("200.002004", "instance.backup.not_allow_operator"),

    DELETE_BACKUP_UPSUPPORT("002999", "rds.highgo.backup.delete_backup_unsupport"), // 不支持手动删除备份
    WAIT_BACKUP_INIT("002998", "rds.highgo.backup.wait_backup_init"); // 等待备份初始化

    private String code;
    private String message;

    BackupError(String code, String message) {
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

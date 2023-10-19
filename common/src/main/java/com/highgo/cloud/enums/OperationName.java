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

public enum OperationName {

    // 创建实例
    CREATE_INSTANCE

    // 删除实例
    , DELETE_INSTANCE

    // 实例规格变更
    , MODIFY_INSTANCE

    // 实例存储扩容
    , EXTEND_STORAGE

    // 实例重启
    , RESTART_INSTANCE

    // 实例开启外网
    , OPEN_NODEPORT

    // 实例关闭外网
    , CLOSE_NODEPORT

    // 创建备份
    , CREATE_BACKUP

    // 删除备份
    , DELETE_BACKUP

    // 恢复备份
    , RESTORE_BACKUP

    // 清空数据
    , PURGE

    // 参数变更
    , CONFIG_CHANGE
}

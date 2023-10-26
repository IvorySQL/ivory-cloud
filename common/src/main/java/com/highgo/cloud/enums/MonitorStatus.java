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

public enum MonitorStatus {

    // 创建中
    CREATING

    // 创建失败
    , CREATE_FAILED

    // 运行中
    , RUNNING

    // 重启中
    , RESTARTING

    // 删除中
    , DELETING

    // 已删除
    , DELETED

    // 删除失败
    , DELETE_FAILED

    // 更配/升级规格中
    , UPGRADING

    // 更配/升级规格失败
    , UPGRADE_FLAVOR_FAILED

    // 扩容中
    , EXTENDING

    // 扩容失败
    , EXTEND_STORAGE_FAILED

    // 重启失败
    , RESTART_FAILED

    // 更新配置中
    , CONFIG_CHANGING

    // 更新配置失败
    , CONFIG_CHANGE_FAILED

    // 异常
    , ERROR

    // 清空数据失败
    , PURGE_FAILED

    // 清空数据中
    , PURGING

}

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

package com.highgo.cloud.constant;

import com.highgo.cloud.enums.BackupStatus;
import com.highgo.cloud.enums.InstanceStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class InstanceAllowConstant {

    public static final List<String> ERROR_INSTANCE_STATUS = new ArrayList<>(Arrays.asList(
            InstanceStatus.CREATE_FAILED.name(),
            InstanceStatus.DELETE_FAILED.name(),
            InstanceStatus.RESTORE_FAILED.name(),
            InstanceStatus.UPGRADE_FLAVOR_FAILED.name(),
            InstanceStatus.EXTEND_STORAGE_FAILED.name(),
            InstanceStatus.RESTART_FAILED.name(),
            InstanceStatus.CONFIG_CHANGE_FAILED.name(),
            InstanceStatus.ERROR.name()));

    /**
     * 允许被恢复的实例状态
     */
    public static final List<InstanceStatus> ALLOW_RESTORE_INSTANCE_STATUS = new ArrayList<>(Arrays.asList(
            InstanceStatus.RUNNING));

    /**
     * 允许被恢复的备份状态
     */
    public static final List<BackupStatus> ALLOW_RESTORE_BACKUP_STATUS = new ArrayList<>(Arrays.asList(
            BackupStatus.COMPLETED,
            BackupStatus.RESTORED,
            BackupStatus.RESTORE_FAILED));

    /**
     * 允许被删除的实例状态
     */
    public static final List<InstanceStatus> ALLOW_DELETE_INSTANCE_STATUS = new ArrayList<>(Arrays.asList(
            InstanceStatus.RUNNING,
            InstanceStatus.STOPPED,
            InstanceStatus.DELETE_FAILED,
            InstanceStatus.DELETING,
            InstanceStatus.ERROR,
            InstanceStatus.CREATE_FAILED,
            InstanceStatus.RESTART_FAILED,
            InstanceStatus.RESTORE_FAILED,
            InstanceStatus.CONFIG_CHANGE_FAILED,
            InstanceStatus.EXTEND_STORAGE_FAILED));

    /**
     * 允许被重启的实例状态
     */
    public static final List<InstanceStatus> ALLOW_RESTART_INSTANCE_STATUS = new ArrayList<>(Arrays.asList(
            InstanceStatus.RUNNING,
            InstanceStatus.RESTART_FAILED));

    /**
     * 允许被停止的实例状态
     */
    public static final List<InstanceStatus> ALLOW_STOP_INSTANCE_STATUS = new ArrayList<>(Arrays.asList(
            InstanceStatus.RUNNING));

    /**
     * 允许规格变更/磁盘扩容的实例状态
     */
    public static final List<InstanceStatus> ALLOW_MODIFY_INSTANCE_STATUS = new ArrayList<>(Arrays.asList(
            InstanceStatus.RUNNING,
            InstanceStatus.UPGRADE_FLAVOR_FAILED,
            InstanceStatus.EXTEND_STORAGE_FAILED,
            InstanceStatus.NODEPORT_OPEN_FAILED,
            InstanceStatus.NODEPORT_CLOSE_FAILED));

    /**
     * 允许执行备份操作的实例状态
     */
    public static final List<InstanceStatus> ALLOW_BACKUP_INSTANCE_STATUS = new ArrayList<>(Arrays.asList(
            InstanceStatus.RUNNING));

    /**
     * 允许删除的备份状态
     */
    public static final List<BackupStatus> ALLOW_DELETE_BACKUP_STATUS = new ArrayList<>(Arrays.asList(
            BackupStatus.COMPLETED,
            BackupStatus.FAILED,
            BackupStatus.DELETE_FAILED));

    /**
     * 允许更配的状态
     */
    public static final List<InstanceStatus> ALLOW_CONFIG_CHANGE_STATUS = new ArrayList<>(Arrays.asList(
            InstanceStatus.RUNNING,
            InstanceStatus.CONFIG_CHANGE_FAILED));

    /**
     * 允许弹性伸缩的状态
     */
    public static final List<InstanceStatus> ALLOW_AUTOSCALING_STATUS = new ArrayList<>(Arrays.asList(
            InstanceStatus.RUNNING,
            InstanceStatus.AUTO_SCALING));

    /**
     * 允许修改密码的状态
     */
    public static final List<InstanceStatus> ALLOW_PASSWORD_CHANGE_STATUS = new ArrayList<>(Arrays.asList(
            InstanceStatus.RUNNING));

    /**
     * 允许主备切换的状态
     */
    public static final List<InstanceStatus> ALLOW_SWITCHOVER_STATUS = new ArrayList<>(Arrays.asList(
            InstanceStatus.RUNNING));

    /**
     * 允许pdr开关切换的状态
     */
    public static final List<InstanceStatus> ALLOW_PDR_SWITCH_STATUS = new ArrayList<>(Arrays.asList(
            InstanceStatus.RUNNING));

    /**
     * 允许ha集群同步的状态
     */
    public static final List<InstanceStatus> ALLOW_HA_SYNC_STATUS = new ArrayList<>(Arrays.asList(
            InstanceStatus.RUNNING,
            InstanceStatus.BACKUPING,
            InstanceStatus.PASSWORD_CHANGING,
            InstanceStatus.ERROR));

}

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

package com.highgo.platform.apiserver.service;

import com.highgo.cloud.enums.BackupStatus;
import com.highgo.cloud.model.PageInfo;
import com.highgo.platform.apiserver.model.dto.BackupDTO;
import com.highgo.platform.apiserver.model.po.BackupPolicyPO;
import com.highgo.platform.apiserver.model.vo.request.CreateBackupVO;
import com.highgo.platform.apiserver.model.vo.request.ModifyAutoBackupSwitchVO;
import com.highgo.platform.apiserver.model.vo.request.ModifyBackupPolicyVO;
import com.highgo.platform.apiserver.model.vo.response.ActionResponse;
import com.highgo.platform.apiserver.model.vo.response.BackupPolicyVO;
import com.highgo.platform.apiserver.model.vo.response.BackupVO;

import java.util.Date;
import java.util.List;

public interface BackupService {

    /**
     * 创建备份
     * @param id
     * @param createBackupParam
     * @return
     */
    public BackupVO createBackup(String id, CreateBackupVO createBackupParam);

    /**
     * 备份记录表中插入备份记录
     * @param instanceId
     * @param createBackupParam
     * @return
     */
    public BackupDTO createBackupRecord(String instanceId, CreateBackupVO createBackupParam);

    /**
     * 创建备份完成回调方法
     * @param filename 备份文件名称
     * @param backupId 备份id
     * @param result 备份结果
     */
    public void createBackupCallback(String backupId, String filename, boolean result);

    /**
     * 创建备份完成回调方法
     * @param backupId 备份id
     * @param result 备份结果
     */
    public void createBackupCallback(String backupId, boolean result);

    /**
     * 查询备份策略
     * @param id
     * @return
     */
    public BackupPolicyVO getBackupPolicy(String id);

    /**
     * 修改备份策略
     * @param id
     * @param modifyBackupPolicyParam
     * @return
     */
    public BackupPolicyVO modifyBackupPolicy(String id, ModifyBackupPolicyVO modifyBackupPolicyParam);

    /**
     * 开启/关闭自动备份
     * @param id
     * @param modifyAutoBackupSwitchParam
     * @return
     */
    public ActionResponse modifyBackupSwitch(String id, ModifyAutoBackupSwitchVO modifyAutoBackupSwitchParam);

    /**
     * 备份分页查询
     * @param pageNo
     * @param pageSize
     * @param filter
     * @return
     */
    public PageInfo<List<BackupVO>> listBackup(int pageNo, int pageSize, String instanceId, String filter);

    /**
     * 删除备份
     * @param id 实例id
     * @param backupId 备份id
     * @return
     */
    public ActionResponse deleteBackup(String id, String backupId);

    /**
     * 删除备份回调方法
     * @param backupid
     * @param result
     */
    public void deleteBackupCallback(String backupid, boolean result);

    /**
     * 根据备份id查询备份信息
     * @param backupId
     * @return
     */
    public BackupDTO getBackupByBackupId(String backupId);

    /**
     * 更新备份文件名
     * @param backupId
     * @param backupFile
     */
    public void updateBackupFile(String backupId, String backupFile);

    /**
     * 通过实例id查询备份不分页清单
     * @param instanceId
     * @return
     */
    public List<BackupDTO> listBackupByInstanceId(String instanceId);

    public BackupDTO getBackupByCreateTime(String instanceId, Date createTime);

    public BackupPolicyPO initBackupPolicy(String instanceId);

    public BackupDTO getBackupByName(String instanceId, String name);

    public void updateBackupStatus(String backupId, BackupStatus backupStatus);

    public void updateBackupIsRestoring(String backupId, Boolean isRestoring);
}

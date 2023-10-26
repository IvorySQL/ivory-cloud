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

package com.highgo.platform.apiserver.service.impl;

import com.highgo.platform.apiserver.model.dto.BackupDTO;
import com.highgo.platform.apiserver.model.dto.InstanceDTO;
import com.highgo.platform.apiserver.model.vo.request.RestoreInstanceVO;
import com.highgo.platform.apiserver.model.vo.response.ActionResponse;
import com.highgo.platform.apiserver.service.BackupService;
import com.highgo.platform.apiserver.service.InstanceService;
import com.highgo.platform.apiserver.service.RestoreService;
import com.highgo.cloud.enums.BackupStatus;
import com.highgo.cloud.enums.InstanceStatus;
import com.highgo.platform.errorcode.BackupError;
import com.highgo.platform.exception.BackupException;
import com.highgo.platform.operator.service.CrService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RestoreServiceImpl implements RestoreService {

    private static final Logger logger = LoggerFactory.getLogger(RestoreServiceImpl.class);

    @Autowired
    private InstanceService instanceService;

    @Autowired
    private BackupService backupService;

    @Autowired
    private CrService crService;

    /**
     * 恢复到当前实例
     * @param id 实例id
     * @param restoreInstanceVO 恢复参数
     * @return
     */
    @Override
    public ActionResponse restoreInstance(String id, RestoreInstanceVO restoreInstanceVO) {
        // 权限校验
        InstanceDTO instanceDTO = instanceService.beforeOperateInstance(id);
        // 查询backupdto
        BackupDTO backupDTO = backupService.getBackupByBackupId(restoreInstanceVO.getBackupId());
        if (backupDTO == null) {
            throw new BackupException(BackupError.BACKUP_NOT_EXIST);
        }
        // 设置实例状态恢复中
        instanceService.updateInstanceStatus(id, InstanceStatus.RESTORING);
        // 设置备份状态恢复中
        backupService.updateBackupStatus(id, BackupStatus.RESTORING);
        // 下发恢复任务到cr
        instanceDTO.setOriginalBackupId(restoreInstanceVO.getBackupId());
        crService.restore(instanceDTO);
        return new ActionResponse(200, true, "数据库恢复中，请耐心等待", InstanceStatus.RESTORING);
    }

    /**
     * 恢复完成回调方法
     * @param id 实例id
     * @param originalBackupId 备份id
     * @param result 恢复结果 成功-true 失败-false
     */
    @Override
    public void restoreInstanceCallBack(String id, String originalBackupId, boolean result) {
        // 设置实例状态 true-running false-restorefailed
        // 设置备份状态 true-restoresuccess false-restorefailed
        try {
            Thread.sleep(1000 * 20);
        } catch (InterruptedException e) {
            logger.error("restoreInstanceCallBack sleep interrupted", e);
        }
        instanceService.updateInstanceStatus(id, InstanceStatus.RUNNING);
        if (result) {
            backupService.updateBackupStatus(originalBackupId, BackupStatus.RESTORED);

        } else {
            backupService.updateBackupStatus(id, BackupStatus.RESTORE_FAILED);
        }
    }
}

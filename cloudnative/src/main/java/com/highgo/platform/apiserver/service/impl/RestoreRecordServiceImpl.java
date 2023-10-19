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

import com.highgo.cloud.util.BeanUtil;
import com.highgo.platform.apiserver.model.dto.RestoreRecordDTO;
import com.highgo.platform.apiserver.model.po.RestoreRecordPO;
import com.highgo.platform.apiserver.repository.RestoreRecordRepository;
import com.highgo.platform.apiserver.service.RestoreRecordService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * @author srk
 * @version 1.0
 * @project micro_service_cloud
 * @description 数据库恢复记录操作实现类
 * @date 2023/9/25 17:39:12
 */
@Service
public class RestoreRecordServiceImpl implements RestoreRecordService {

    @Resource
    private RestoreRecordRepository restoreRecordRepository;

    @Override
    public RestoreRecordDTO getRestoreRecordByInstanceId(String instanceId) {
        Optional<RestoreRecordPO> restoreRecord = restoreRecordRepository.findRestoreRecordPOByInstanceId(instanceId);
        if (restoreRecord.isPresent()) {
            RestoreRecordDTO dto = new RestoreRecordDTO();
            BeanUtil.copyNotNullProperties(restoreRecord.get(), dto);
            return dto;
        }
        return null;
    }

    @Override
    public void createOrModifyRestoreRecord(RestoreRecordDTO restoreRecordDTO) {
        if (restoreRecordDTO == null) {
            return;
        }
        RestoreRecordPO po = new RestoreRecordPO();
        BeanUtil.copyNotNullProperties(restoreRecordDTO, po);
        restoreRecordRepository.save(po);
    }

}

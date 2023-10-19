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

package com.highgo.platform.operator.service.impl;

import com.highgo.platform.apiserver.model.dto.BackupDTO;
import com.highgo.platform.apiserver.model.dto.BackupPolicyDTO;
import com.highgo.platform.apiserver.model.dto.InstanceDTO;
import com.highgo.platform.apiserver.model.dto.InstanceNetworkDTO;
import com.highgo.platform.apiserver.service.BackupService;
import com.highgo.platform.apiserver.service.ConfigService;
import com.highgo.platform.apiserver.service.InstanceService;
import com.highgo.platform.operator.service.CrService;
import com.highgo.cloud.enums.NetworkType;
import com.highgo.cloud.enums.SwitchStatus;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.CustomResource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 *  crservice demo, 数据库项目需根据产品特性实现CrService，并注册为service
 */
// @Service //启动dbbase项目调试时，需要反注释此行
public class TestCrServiceImpl implements CrService {

    @Autowired
    InstanceService instanceService;

    @Autowired
    BackupService backupService;

    @Autowired
    ConfigService configService;

    /**
     * 创建CR，若cr已存在，则退出
     *
     * @param instanceDTO
     * @return
     */
    @Override
    public boolean createCr(InstanceDTO instanceDTO) {
        // if cr exist, return
        System.out.println("cr创建中。。。");
        return false;
    }

    /**
     * 更新CR,构建整个cr
     *
     * @param instanceDTO
     * @return 执行结果 成功true 失败false
     */
    @Override
    public boolean applyCr(InstanceDTO instanceDTO) {
        String instanceId = instanceDTO.getId();
        InstanceNetworkDTO instanceNetworkDTO = new InstanceNetworkDTO();
        instanceNetworkDTO.setInstanceId(instanceId);
        instanceNetworkDTO.setPort(5432);
        instanceNetworkDTO.setService("instance-master.cnp-system.local");
        instanceNetworkDTO.setType(NetworkType.RW);
        InstanceNetworkDTO instanceNetworkDTO1 = new InstanceNetworkDTO();
        instanceNetworkDTO1.setInstanceId(instanceId);
        instanceNetworkDTO1.setPort(5432);
        instanceNetworkDTO1.setService("instance-salve.cnp-system.local");
        instanceNetworkDTO1.setType(NetworkType.RO);
        List<InstanceNetworkDTO> instanceNetworkDTOS = new ArrayList<>();
        instanceNetworkDTOS.add(instanceNetworkDTO);
        instanceNetworkDTOS.add(instanceNetworkDTO1);
        instanceDTO.setNetwork(instanceNetworkDTOS);

        instanceService.createInstanceCallback(instanceId, instanceNetworkDTOS, "", "", true);

        return false;
    }

    /**
     * 校验CR是否已存在
     *
     * @param namespace 命名空间名称
     * @param crName    CR名称
     * @return 存在-true 不存在-false
     */
    @Override
    public boolean isCrExist(String clusterId, String namespace, String crName) {
        return false;
    }

    /**
     * patch cr的resource字段(cpu memory)
     *
     * @param instanceDTO
     * @return
     */
    @Override
    public boolean patchCrResource(InstanceDTO instanceDTO) {
        instanceService.modifyInstanceCallback(instanceDTO.getId(), true);
        return false;
    }

    /**
     * patch cr的storage字段
     *
     * @param instanceDTO
     * @return
     */
    @Override
    public boolean patchCrStorage(InstanceDTO instanceDTO) {
        instanceService.extendInstanceCallback(instanceDTO.getId(), true);
        return false;
    }

    /**
     * 删除CR
     *
     * @param instanceDTO
     * @return 执行结果 成功true 失败false
     */
    @Override
    public boolean deleteCr(InstanceDTO instanceDTO) {
        System.out.println("删除CR中");
        // =====test====
        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
        }
        System.out.println("CR已删除");
        instanceService.deleteInstanceCallback(instanceDTO.getId(), true);

        return false;
    }

    /**
     * 删除实例的所有pod(重启实例时，删除pod进行重启)
     *
     * @param instanceDTO
     * @return
     */
    @Override
    public boolean deleteAllPod(InstanceDTO instanceDTO) {
        instanceService.restartInstanceCallback(instanceDTO.getId(), true);
        return false;
    }

    @Override
    public boolean restartDatabase(InstanceDTO instanceDTO) {
        return false;
    }

    @Override
    public boolean nodeportSwitch(InstanceDTO instanceDTO) {
        if (instanceDTO.getNodePortSwitch().equals(SwitchStatus.OFF)) {
            instanceService.closeNodeportSwitchCallback(instanceDTO.getId(), true);
        } else {
            instanceService.openNodeportSwitchCallback(instanceDTO.getId(), 5555, 6666, true);
        }
        return true;
    }

    /**
     * 创建备份
     * @param backupDTO
     * @return
     */
    @Override
    public boolean createBackup(BackupDTO backupDTO) {
        System.out.println("创建备份中");
        backupService.createBackupCallback(backupDTO.getId(), "filename", true);
        return true;
    }

    /**
     * 更新自动备份策略
     * @param backupPolicyDTO
     * @return
     */
    @Override
    public boolean applyBackupPolicy(BackupPolicyDTO backupPolicyDTO) {
        System.out.println("更新自动备份策略中...");
        return true;
    }

    /**
     * 删除备份
     * @param backupDTO
     * @return
     */
    @Override
    public boolean deleteBackup(BackupDTO backupDTO) {
        System.out.println("删除备份中。。。");
        return true;
    }

    @Override
    public boolean applyConfigParam(InstanceDTO instanceDTO) {
        System.out.println("配置变更中。。。");
        configService.modifyParametersCallback(instanceDTO.getId(), instanceDTO.getConfigChangeHistoryId(), true);
        return false;
    }

    /**
     * 从CR中解析InstanceDTO
     *
     * @param customResource
     * @return
     */
    @Override
    public InstanceDTO getInstanceVOFromCR(CustomResource customResource) {
        return new InstanceDTO();
    }

    /**
     * 获取master 节点pod
     *
     * @param instanceDTO
     * @return
     */
    @Override
    public Pod getMasterPod(InstanceDTO instanceDTO) {
        return new Pod();
    }

    @Override
    public boolean restore(InstanceDTO instanceDTO) {
        System.out.println("恢复中。。。");
        return true;
    }

    @Override
    public Integer getHgadminPort(String instanceId) {
        return null;
    }
}

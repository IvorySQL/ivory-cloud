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

import static org.mockito.ArgumentMatchers.any;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.highgo.cloud.enums.BackupStatus;
import com.highgo.cloud.enums.InstanceStatus;
import com.highgo.platform.apiserver.model.po.BackupPO;
import com.highgo.platform.apiserver.model.po.InstancePO;
import com.highgo.platform.apiserver.model.vo.request.CreateInstanceVO;
import com.highgo.platform.apiserver.model.vo.response.InstanceVO;
import com.highgo.platform.apiserver.repository.BackupPolicyRepository;
import com.highgo.platform.apiserver.repository.BackupRepository;
import com.highgo.platform.apiserver.repository.ConfigChangeHistoryRepository;
import com.highgo.platform.apiserver.repository.ConfigDefinationRepository;
import com.highgo.platform.apiserver.repository.ConfigInstanceParamRepository;
import com.highgo.platform.apiserver.repository.InstanceEventRepository;
import com.highgo.platform.apiserver.repository.InstanceNetworkRepository;
import com.highgo.platform.apiserver.repository.InstanceRepository;
import com.highgo.platform.apiserver.repository.K8sClusterInfoRepository;
import com.highgo.platform.apiserver.service.BackupService;
import com.highgo.platform.apiserver.service.ExtraMetaService;
import com.highgo.platform.apiserver.service.K8sClusterService;
import com.highgo.platform.configuration.K8sClientConfiguration;
import com.highgo.platform.errorcode.BackupError;
import com.highgo.platform.errorcode.InstanceError;
import com.highgo.platform.exception.BackupException;
import com.highgo.platform.exception.InstanceException;
import com.highgo.platform.operator.service.CrService;
import com.highgo.platform.operator.watcher.WatcherFactory;
import com.highgo.platform.websocket.service.WebsocketService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class InstanceServiceImplTest {

    @InjectMocks
    private InstanceServiceImpl instanceServiceImpl;

    @Mock
    private InstanceRepository instanceRepository;

    @Mock
    InstanceNetworkRepository instanceNetworkRepository;

    @Mock
    K8sClusterInfoRepository k8sClusterInfoRepository;

    @Mock
    BackupPolicyRepository backupPolicyRepository;

    @Mock
    BackupRepository backupRepository;

    @Mock
    ConfigDefinationRepository configDefinationRepository;

    @Mock
    ConfigInstanceParamRepository configInstanceParamRepository;

    @Mock
    ConfigChangeHistoryRepository configChangeHistoryRepository;

    @Mock
    InstanceEventRepository instanceEventRepository;

    @Mock
    private EntityManager entityManager;

    @Mock
    WatcherFactory watcherFactory;

    @Mock
    CrService crService;

    @Mock
    K8sClusterService k8sClusterService;

    @Mock
    K8sClientConfiguration k8sClientConfiguration;

    @Mock
    ExtraMetaService extraMetaService;

    @Mock
    WebsocketService websocketService;

    @Mock
    BackupService backupService;

    private String clusterId = "123456";
    private String namespace = "nstest";
    private String instanceName = "ivory";

    @Test
    void testCreateInstanceCreateInstanceVO() {
        CreateInstanceVO createInstanceVO = new CreateInstanceVO();
        createInstanceVO.setClusterId(clusterId);
        createInstanceVO.setNamespace(namespace);
        createInstanceVO.setName(instanceName);

        // 该实例已经创建
        when(instanceRepository.countByClusterAndNamespaceAndName(clusterId, namespace, instanceName)).thenReturn(1);
        // doReturn(1).when(instanceRepository).countByClusterAndNamespaceAndName(clusterId,
        // namespace, instanceName);

        InstanceException exception = assertThrows(InstanceException.class, () -> {
            // 在这里调用该方法
            instanceServiceImpl.createInstance(createInstanceVO);
        });

        assertEquals(InstanceError.DUPLICATE_NAME.message(), exception.getMessage());

        // 测试 实例未曾创建
        when(instanceRepository.countByClusterAndNamespaceAndName(clusterId, namespace, instanceName)).thenReturn(0);

        InstancePO instancePO = new InstancePO();
        instancePO.setId("poId");

        when(instanceRepository.save((InstancePO) any(InstancePO.class))).thenReturn(instancePO);
        InstanceVO instanceVO = instanceServiceImpl.createInstance(createInstanceVO);
        assertEquals(instanceName, instanceVO.getName());

        // 测试恢复模式
        // 实例不存在或已删除
        createInstanceVO.setOriginalInstanceId("originalInstanceId");
        InstanceException exception_instance_not_exist = assertThrows(InstanceException.class, () -> {
            // 在这里调用该方法
            instanceServiceImpl.createInstance(createInstanceVO);
        });

        assertEquals(InstanceError.INSTANCE_NOT_EXIST.message(), exception_instance_not_exist.getMessage());

        // 实例已删除
        when(instanceRepository.findById(any(String.class))).thenReturn(getDeletedInstances());
        InstanceException exception_instance_is_deleted = assertThrows(InstanceException.class, () -> {
            // 在这里调用该方法
            instanceServiceImpl.createInstance(createInstanceVO);
        });
        assertEquals(InstanceError.INSTANCE_NOT_EXIST.message(), exception_instance_is_deleted.getMessage());

        // 实例存在,但是当前状态不允许执行恢复
        when(instanceRepository.findById(any(String.class))).thenReturn(getNotDeleted_creating_Instances());
        InstanceException exception_instance_nofile_not_allow_restore = assertThrows(InstanceException.class, () -> {
            // 在这里调用该方法
            instanceServiceImpl.createInstance(createInstanceVO);
        });

        assertEquals(InstanceError.INSTANCE_NOT_ALLOW_OPERATE.message(),
                exception_instance_nofile_not_allow_restore.getMessage());

        // 实例存在,但是备份不存在
        when(instanceRepository.findById(any(String.class))).thenReturn(getRunning_Instances());
        BackupException exception_instance_backupfile_not_exist = assertThrows(BackupException.class, () -> {
            // 在这里调用该方法
            instanceServiceImpl.createInstance(createInstanceVO);
        });
        assertEquals(BackupError.BACKUP_NOT_EXIST.message(), exception_instance_backupfile_not_exist.getMessage());

        // 实例存在,备份文件也存在, 但是备份当前状态不允许执行恢复
        createInstanceVO.setOriginalBackupId("originalBackupId");
        Optional<BackupPO> optionalBackupPO = getBackupFiles_notAllowRestoreOptional();
        when(backupRepository.findById(any(String.class))).thenReturn(optionalBackupPO);
        BackupException exception_instance_not_allow_restore = assertThrows(BackupException.class, () -> {
            // 在这里调用该方法
            instanceServiceImpl.createInstance(createInstanceVO);
        });
        assertEquals(BackupError.BACKUP_NOT_ALLOW_OPERATE.message(), exception_instance_not_allow_restore.getMessage());

        // 新的实例cpu大小 < 老实例CPU 大小，不允许
        // 新创建的实例
        createInstanceVO.setCpu(1);
        optionalBackupPO.get().setStatus(BackupStatus.COMPLETED);
        when(instanceRepository.findById(any(String.class))).thenReturn(getCpuLargerThanNewInstance());
        InstanceException exception_cpu = assertThrows(InstanceException.class, () -> {
            // 在这里调用该方法
            instanceServiceImpl.createInstance(createInstanceVO);
        });
        assertEquals(InstanceError.INSTANCE_NOT_ALLOW_DEMOTE.message(), exception_cpu.getMessage());

        // 新的实例memory大小 < 老实例memory 大小，不允许
        // 新创建的实例
        createInstanceVO.setCpu(1);
        createInstanceVO.setMemory(1);
        optionalBackupPO.get().setStatus(BackupStatus.COMPLETED);
        when(instanceRepository.findById(any(String.class))).thenReturn(getMemoryLargerThanNewInstance());
        InstanceException exception_memory = assertThrows(InstanceException.class, () -> {
            // 在这里调用该方法
            instanceServiceImpl.createInstance(createInstanceVO);
        });
        assertEquals(InstanceError.INSTANCE_NOT_ALLOW_DEMOTE.message(), exception_memory.getMessage());

        // 新的实例storage大小 < 老实例storage 大小，不允许
        // 新创建的实例
        createInstanceVO.setCpu(1);
        createInstanceVO.setMemory(1);
        createInstanceVO.setStorage(1);
        optionalBackupPO.get().setStatus(BackupStatus.COMPLETED);
        when(instanceRepository.findById(any(String.class))).thenReturn(getStorageLargerThanNewInstance());
        InstanceException exception_storage = assertThrows(InstanceException.class, () -> {
            // 在这里调用该方法
            instanceServiceImpl.createInstance(createInstanceVO);
        });
        assertEquals(InstanceError.INSTANCE_NOT_ALLOW_DEMOTE.message(), exception_storage.getMessage());

    }

    private Optional<InstancePO> getDeletedInstances() {
        InstancePO instancePO = new InstancePO();
        instancePO.setIsDeleted(true);
        Optional<InstancePO> optionalInstancePo = Optional.of(instancePO);

        return optionalInstancePo;
    }

    private Optional<InstancePO> getNotDeleted_creating_Instances() {
        InstancePO instancePO = new InstancePO();
        instancePO.setIsDeleted(false);
        instancePO.setStatus(InstanceStatus.CREATING);
        Optional<InstancePO> optionalInstancePo = Optional.of(instancePO);

        return optionalInstancePo;
    }

    private Optional<InstancePO> getRunning_Instances() {
        InstancePO instancePO = new InstancePO();
        instancePO.setIsDeleted(false);
        instancePO.setStatus(InstanceStatus.RUNNING);
        Optional<InstancePO> optionalInstancePo = Optional.of(instancePO);

        return optionalInstancePo;
    }

    /**
     * 获取老实例， 其cpu 比新建实例的大
     * @return
     */
    private Optional<InstancePO> getCpuLargerThanNewInstance() {
        InstancePO instancePO = new InstancePO();
        instancePO.setIsDeleted(false);
        instancePO.setStatus(InstanceStatus.RUNNING);
        instancePO.setCpu(10);
        Optional<InstancePO> optionalInstancePo = Optional.of(instancePO);

        return optionalInstancePo;
    }

    /**
     * 获取老实例， 其memory 比新建实例的大
     * @return
     */
    private Optional<InstancePO> getMemoryLargerThanNewInstance() {
        InstancePO instancePO = new InstancePO();
        instancePO.setIsDeleted(false);
        instancePO.setStatus(InstanceStatus.RUNNING);
        instancePO.setCpu(1);
        instancePO.setMemory(10);
        Optional<InstancePO> optionalInstancePo = Optional.of(instancePO);

        return optionalInstancePo;
    }

    /**
     * 获取老实例， 其storage 比新建实例的大
     * @return
     */
    private Optional<InstancePO> getStorageLargerThanNewInstance() {
        InstancePO instancePO = new InstancePO();
        instancePO.setIsDeleted(false);
        instancePO.setStatus(InstanceStatus.RUNNING);
        instancePO.setCpu(1);
        instancePO.setMemory(1);
        instancePO.setStorage(10);
        Optional<InstancePO> optionalInstancePo = Optional.of(instancePO);

        return optionalInstancePo;
    }

    private Optional<BackupPO> getBackupFiles_notAllowRestoreOptional() {
        BackupPO backupPO = new BackupPO();
        backupPO.setStatus(BackupStatus.PROCESSING);
        Optional<BackupPO> optionalBackupPo = Optional.of(backupPO);

        return optionalBackupPo;
    }

    @Test
    void testCreateInstanceInstanceDTO() {

    }

    @Test
    void testCreateInstanceCallback() {

    }

    @Test
    void testDeleteInstance() {

    }

    @Test
    void testDeleteInstanceCallback() {

    }

    @Test
    void testGetVO() {

    }

    @Test
    void testGetDTO() {

    }

    @Test
    void testListByFilter() {

    }

    @Test
    void testGetInstanceCount() {

    }

    @Test
    void testGetInstanceCountByUser() {

    }

    // @Test
    // void testList() {
    //
    // }
    //
    // @Test
    // void testModifyInstanceDescription() {
    //
    // }
    //
    // @Test
    // void testRestartInstance() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testRestartInstanceCallback() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testModifyInstance() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testModifyInstanceCallback() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testExtendInstance() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testExtendInstanceCallback() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testModifyNodeportSwitch() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testOpenNodeportSwitchCallback() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testCloseNodeportSwitchCallback() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testBeforeOperateInstance() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testInstanceNameUniqueCheck() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testGetEvent() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testUpdateResourseVersion() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testGetResourceVersion() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testUpdateNodeNum() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testUpdateStsEvent() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testUpdatePodEvent() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testUpdateNodeReadyNum() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testUpdateNodeEvent() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testGetInstanceStatus() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testUpdateInstanceStatus() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testGetStorageClasses() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testGetMasterPod() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testGetProjectIdByNamespace() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testGetSpecialExtraMeta() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testGetHgadminUrl() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testCreateInstanceHgadminCallback() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testUpdateInstanceCpuResource() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testUpdateInstanceMemoryResource() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testExtrametaHandler() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testValidataBeforeRestore() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testIsInstanceExistByClusterAndNamespaceAndName() {
    // fail("Not yet implemented");
    // }

}

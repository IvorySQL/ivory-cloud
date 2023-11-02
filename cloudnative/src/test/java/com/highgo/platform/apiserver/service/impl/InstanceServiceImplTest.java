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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import com.highgo.cloud.enums.BackupStatus;
import com.highgo.cloud.enums.InstanceStatus;
import com.highgo.platform.apiserver.model.dto.InstanceDTO;
import com.highgo.platform.apiserver.model.po.BackupPO;
import com.highgo.platform.apiserver.model.po.ExtraMetaPO;
import com.highgo.platform.apiserver.model.po.InstanceNetworkPO;
import com.highgo.platform.apiserver.model.po.InstancePO;
import com.highgo.platform.apiserver.model.po.K8sClusterInfoPO;
import com.highgo.platform.apiserver.model.vo.request.CreateInstanceVO;
import com.highgo.platform.apiserver.model.vo.response.ActionResponse;
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
    void testCreateInstance() {
        CreateInstanceVO createInstanceVO = new CreateInstanceVO();
        createInstanceVO.setClusterId(clusterId);
        createInstanceVO.setNamespace(namespace);
        createInstanceVO.setName(instanceName);

        // 该实例已经创建
        when(instanceRepository.countByClusterAndNamespaceAndName(clusterId, namespace, instanceName)).thenReturn(1);
        // doReturn(1).when(instanceRepository).countByClusterAndNamespaceAndName(clusterId,
        // namespace, instanceName);

        InstanceException exception = assertThrows(InstanceException.class, () -> {
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
            instanceServiceImpl.createInstance(createInstanceVO);
        });

        assertEquals(InstanceError.INSTANCE_NOT_EXIST.message(), exception_instance_not_exist.getMessage());

        // 实例已删除
        when(instanceRepository.findById(any(String.class))).thenReturn(getDeletedInstances());
        InstanceException exception_instance_is_deleted = assertThrows(InstanceException.class, () -> {
            instanceServiceImpl.createInstance(createInstanceVO);
        });
        assertEquals(InstanceError.INSTANCE_NOT_EXIST.message(), exception_instance_is_deleted.getMessage());

        // 实例存在,但是当前状态不允许执行恢复
        when(instanceRepository.findById(any(String.class))).thenReturn(getNotDeleted_creating_Instances());
        InstanceException exception_instance_nofile_not_allow_restore = assertThrows(InstanceException.class, () -> {
            instanceServiceImpl.createInstance(createInstanceVO);
        });

        assertEquals(InstanceError.INSTANCE_NOT_ALLOW_OPERATE.message(),
                exception_instance_nofile_not_allow_restore.getMessage());

        // 实例存在,但是备份不存在
        when(instanceRepository.findById(any(String.class))).thenReturn(getRunning_Instances());
        BackupException exception_instance_backupfile_not_exist = assertThrows(BackupException.class, () -> {
            instanceServiceImpl.createInstance(createInstanceVO);
        });
        assertEquals(BackupError.BACKUP_NOT_EXIST.message(), exception_instance_backupfile_not_exist.getMessage());

        // 实例存在,备份文件也存在, 但是备份当前状态不允许执行恢复
        createInstanceVO.setOriginalBackupId("originalBackupId");
        Optional<BackupPO> optionalBackupPO = getBackupFiles_notAllowRestoreOptional();
        when(backupRepository.findById(any(String.class))).thenReturn(optionalBackupPO);
        BackupException exception_instance_not_allow_restore = assertThrows(BackupException.class, () -> {
            instanceServiceImpl.createInstance(createInstanceVO);
        });
        assertEquals(BackupError.BACKUP_NOT_ALLOW_OPERATE.message(), exception_instance_not_allow_restore.getMessage());

        // 新的实例cpu大小 < 老实例CPU 大小，不允许
        // 新创建的实例
        createInstanceVO.setCpu(1);
        optionalBackupPO.get().setStatus(BackupStatus.COMPLETED);
        when(instanceRepository.findById(any(String.class))).thenReturn(getCpuLargerThanNewInstance());
        InstanceException exception_cpu = assertThrows(InstanceException.class, () -> {
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
     * 
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
     * 
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
     * 
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
    void testDeleteInstance() {
        String instanceId = "123456";
        // 测试实例不存在
        Optional<InstancePO> instancePo_empty = Optional.empty();

        when(instanceRepository.findById(instanceId)).thenReturn(instancePo_empty);
        InstanceException exception = assertThrows(InstanceException.class, () -> {

            instanceServiceImpl.deleteInstance(instanceId);
        });
        assertEquals(InstanceError.INSTANCE_NOT_EXIST.message(), exception.getMessage());

        // 测试: 实例存在 但是实例状态不允许删除
        when(instanceRepository.findById(instanceId)).thenReturn(getNotAllowedDelete_Instances());
        when(extraMetaService.findAllByInstanceId(instanceId)).thenReturn(findExtrameta());
        InstanceException exception_not_allow_deleted = assertThrows(InstanceException.class, () -> {

            instanceServiceImpl.deleteInstance(instanceId);
        });
        assertEquals(InstanceError.INSTANCE_NOT_ALLOW_OPERATE.message(), exception_not_allow_deleted.getMessage());

        // 测试: 成功删除实例
        when(instanceRepository.findById(instanceId)).thenReturn(getAllowedDelete_Instances());
        when(crService.deleteCr(any(InstanceDTO.class))).thenReturn(true);
        ActionResponse actionResponse = instanceServiceImpl.deleteInstance(instanceId);

        assertEquals(actionResponse.getCode(), ActionResponse.actionSuccess().getCode());
    }

    private List<ExtraMetaPO> findExtrameta() {
        ExtraMetaPO extraMetaPO = new ExtraMetaPO();
        extraMetaPO.setId("1");
        extraMetaPO.setName("name1");
        extraMetaPO.setValue("value1");
        List<ExtraMetaPO> extraMetaPOS = new ArrayList<ExtraMetaPO>();
        extraMetaPOS.add(extraMetaPO);

        return extraMetaPOS;

    }

    /**
     * 获取不允许被删除的实例
     * @return
     */
    private Optional<InstancePO> getNotAllowedDelete_Instances() {
        InstancePO instancePO = new InstancePO();
        instancePO.setIsDeleted(false);
        instancePO.setStatus(InstanceStatus.CREATING);
        Optional<InstancePO> optionalInstancePo = Optional.of(instancePO);

        return optionalInstancePo;
    }

    /**
     * 获取允许被删除的实例
     * @return
     */
    private Optional<InstancePO> getAllowedDelete_Instances() {
        InstancePO instancePO = new InstancePO();
        instancePO.setIsDeleted(false);
        instancePO.setStatus(InstanceStatus.RUNNING);
        Optional<InstancePO> optionalInstancePo = Optional.of(instancePO);

        return optionalInstancePo;
    }

    @Test
    void testGetVO() {
        String id = "123456";
        when(instanceRepository.findById(id)).thenReturn(getWithClusterId_Instances());

        List<InstanceNetworkPO> instanceNetworkPOs = getInstanceNetworkPOById();
        when(instanceNetworkRepository.listByInstanceId(id)).thenReturn(instanceNetworkPOs);

        K8sClusterInfoPO k8sClusterInfoPO = new K8sClusterInfoPO();
        k8sClusterInfoPO.setClusterName("clusterName");
        when(k8sClusterService.getInfoByClusterId(any(String.class))).thenReturn(k8sClusterInfoPO);
        InstanceVO instanceVO = instanceServiceImpl.getVO(id);

        assertEquals(instanceVO.getClusterName(), k8sClusterInfoPO.getClusterName());
        assertEquals(instanceVO.getNetwork().size(), instanceNetworkPOs.size());
        assertEquals(instanceVO.getNetwork().get(0).getNodePort(), instanceNetworkPOs.get(0).getNodePort());
    }

    /**
     * 获取允许被删除的实例
     * @return
     */
    private Optional<InstancePO> getWithClusterId_Instances() {
        InstancePO instancePO = new InstancePO();
        instancePO.setIsDeleted(false);
        instancePO.setStatus(InstanceStatus.RUNNING);
        instancePO.setClusterId("clusterId");
        Optional<InstancePO> optionalInstancePo = Optional.of(instancePO);

        return optionalInstancePo;
    }

    /**
     * get instance network
     * @return
     */
    public List<InstanceNetworkPO> getInstanceNetworkPOById() {
        InstanceNetworkPO instanceNetworkPO = new InstanceNetworkPO();
        instanceNetworkPO.setId("1");
        instanceNetworkPO.setNodePort(6060);
        List<InstanceNetworkPO> instanceNetworkPOs = new ArrayList<InstanceNetworkPO>();
        instanceNetworkPOs.add(instanceNetworkPO);

        return instanceNetworkPOs;
    }
    @Test
    void testGetDTO() {
        String instanceId = "123456";
        // 测试实例不存在
        Optional<InstancePO> instancePo_empty = Optional.empty();

        when(instanceRepository.findById(instanceId)).thenReturn(instancePo_empty);
        InstanceException exception = assertThrows(InstanceException.class, () -> {

            instanceServiceImpl.getDTO(instanceId);
        });
        assertEquals(InstanceError.INSTANCE_NOT_EXIST.message(), exception.getMessage());

        // 测试 获取产品的属性
        when(instanceRepository.findById(instanceId)).thenReturn(getWithClusterId_Instances());
        when(extraMetaService.findAllByInstanceId(instanceId)).thenReturn(findExtrameta());
        InstanceDTO instanceDTO = instanceServiceImpl.getDTO(instanceId);
        assertEquals("value1", instanceDTO.getExtraMeta().get("name1"));

    }

    @Test
    void testListByFilter() {
        // List<InstanceNetworkPO> instanceNetworkPOList = instanceNetworkRepository.list();
        when(instanceNetworkRepository.list()).thenReturn(getInstanceNetworkPOList());

        // Page<InstancePO> page = instanceRepository.findAll(specification, pageable);
        Specification<InstancePO> spec = any(Specification.class);
        PageRequest pageable = any(PageRequest.class);

        // 设置mock行为
        // Page<InstancePO> expectedPage = mock(Page.class);
        when(instanceRepository.findAll(spec, pageable)).thenReturn(getInstancePOByPage());

        instanceServiceImpl.listByFilter(1, "filter", "clusterId", 10, 20);
    }

    private List<InstanceNetworkPO> getInstanceNetworkPOList() {
        InstanceNetworkPO instanceNetworkPO1 = new InstanceNetworkPO();
        instanceNetworkPO1.setInstanceId("0001");
        instanceNetworkPO1.setNodePort(6060);

        InstanceNetworkPO instanceNetworkPO2 = new InstanceNetworkPO();
        instanceNetworkPO2.setInstanceId("0002");
        instanceNetworkPO2.setNodePort(6061);

        List<InstanceNetworkPO> instanceNetworkPOList = new ArrayList<InstanceNetworkPO>();
        instanceNetworkPOList.add(instanceNetworkPO1);
        instanceNetworkPOList.add(instanceNetworkPO2);

        return instanceNetworkPOList;
    }

    private Page<InstancePO> getInstancePOByPage() {
        // 创建PageRequest对象，指定页码、每页大小和排序规则（可选）
        PageRequest pageRequest = PageRequest.of(10, 20, Sort.by("name"));

        InstancePO instancePO1 = new InstancePO();
        instancePO1.setId("0001");
        instancePO1.setClusterId("0001");
        instancePO1.setName("name1");

        InstancePO instancePO2 = new InstancePO();
        instancePO1.setId("0002");
        instancePO2.setClusterId("0002");
        instancePO2.setName("name2");

        List<InstancePO> instanceList = new ArrayList<InstancePO>();
        instanceList.add(instancePO1);
        instanceList.add(instancePO2);

        long totalElements = 100;
        Page<InstancePO> page = new PageImpl<>(instanceList, pageRequest, totalElements);

        return page;
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

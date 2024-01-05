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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.highgo.platform.apiserver.model.dto.InstanceDTO;
import com.highgo.platform.apiserver.service.InstanceService;
import com.highgo.platform.configuration.K8sClientConfiguration;
import com.highgo.platform.exception.InstanceException;
import com.highgo.platform.operator.cr.DatabaseCluster;
import com.highgo.platform.operator.cr.bean.DatabaseClusterSpec;
import com.highgo.platform.operator.service.OperatorClusterSpecService;

import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@RunWith(MockitoJUnitRunner.class)
class CrServiceImplTest {

    @InjectMocks
    private CrServiceImpl crServiceImpl;

    @Mock
    private K8sClientConfiguration k8sClientConfiguration;

    @Mock
    private KubernetesClient clientMock;

    @Mock
    private Resource<DatabaseCluster> databaseClusterResource;

    @Mock
    private DatabaseCluster databaseCluster;

    @Mock
    private MixedOperation<DatabaseCluster, KubernetesResourceList, Resource<DatabaseCluster>> resourceOperation;

    @Mock
    private OperatorClusterSpecService operatorClusterSpecService;

    @Mock
    private InstanceService instanceService;

    @Mock
    private ObjectMeta metaData;

    @Mock
    private Map<String, String> labels;

    @Mock
    private DatabaseClusterSpec spec;

    private String clusterId = "123456";
    private String namespace = "nstest";
    private String crName = "test";

    @BeforeEach
    public void setUp() {

        // mock k8s client
        Mockito.when(k8sClientConfiguration.getAdminKubernetesClientById(clusterId)).thenReturn(clientMock);

        doReturn(resourceOperation).when(clientMock).customResources(DatabaseCluster.class);

        doReturn(resourceOperation).when(resourceOperation).inNamespace(namespace);

        doReturn(databaseClusterResource).when(resourceOperation).withName(crName);

        doReturn(databaseCluster).when(databaseClusterResource).get();

    }

    @Test
    void testCreateCr() {
        InstanceDTO instanceDTO = new InstanceDTO();
        instanceDTO.setClusterId(clusterId);
        instanceDTO.setNamespace(namespace);
        instanceDTO.setName(crName);

        // 1. test CR exist
        assertFalse(crServiceImpl.createCr(instanceDTO));

        // 2. test CR not exist
        // 2.1 new CR
        doReturn(null).when(databaseClusterResource).get();

        DatabaseClusterSpec databaseClusterSpec = mock(DatabaseClusterSpec.class);
        doReturn(databaseClusterSpec).when(operatorClusterSpecService).initClusterSpec(instanceDTO);
        assertTrue(crServiceImpl.createCr(instanceDTO));

        // 2.2 恢复出来的新实例restore CR
        String originalInstanceId = "123";
        instanceDTO.setOriginalInstanceId(originalInstanceId);
        instanceDTO.setOriginalBackupId("456");

        // mock original CR
        // 2.2.1 orignal instance namespace is not equal with this current instance
        InstanceDTO originInstanceDTO = new InstanceDTO();

        originInstanceDTO.setNamespace("origianlNamespace");

        when(instanceService.getDTO(originalInstanceId)).thenReturn(originInstanceDTO);

        InstanceException exception = assertThrows(InstanceException.class, () -> {
            // 在这里调用该方法
            crServiceImpl.createCr(instanceDTO);
        });

        // 可以对异常对象进行进一步的断言
        assertEquals("instance.restore_not_in_namespace", exception.getMessage());

        // 2.2.21 orignal instance clusterId is not equal with this current instance
        originInstanceDTO.setNamespace(namespace);
        originInstanceDTO.setClusterId("originalClusterId");
        when(instanceService.getDTO(originalInstanceId)).thenReturn(originInstanceDTO);

        InstanceException exception2 = assertThrows(InstanceException.class, () -> {
            // 在这里调用该方法
            crServiceImpl.createCr(instanceDTO);
        });

        // 可以对异常对象进行进一步的断言
        assertEquals("instance.restore_not_in_namespace", exception2.getMessage());

        // 2.3 new instance: OriginalInstanceId is null
        instanceDTO.setOriginalInstanceId(null);
        assertTrue(crServiceImpl.createCr(instanceDTO));

        // 2.4 new instance: setOriginalBackupId is null
        instanceDTO.setOriginalInstanceId(originalInstanceId);
        instanceDTO.setOriginalBackupId(null);
        assertTrue(crServiceImpl.createCr(instanceDTO));

    }

    @Test
    void testIsCrExist() {

        boolean result = crServiceImpl.isCrExist(clusterId, namespace, crName);

        // test cr exist
        assertTrue(result);

        // test cr not exist
        doReturn(null).when(databaseClusterResource).get();
        assertFalse(crServiceImpl.isCrExist(clusterId, namespace, crName));
    }
    @Test
    void testPatchCrResource() {
        InstanceDTO instanceDTO = new InstanceDTO();
        instanceDTO.setClusterId(clusterId);
        instanceDTO.setNamespace(namespace);
        instanceDTO.setName(crName);

        doReturn(metaData).when(databaseCluster).getMetadata();
        doReturn(labels).when(metaData).getLabels();

        assertTrue(crServiceImpl.patchCrResource(instanceDTO));
    }

    @Test
    void testPatchCrStorage() {
        InstanceDTO instanceDTO = new InstanceDTO();
        instanceDTO.setClusterId(clusterId);
        instanceDTO.setNamespace(namespace);
        instanceDTO.setName(crName);

        doReturn(metaData).when(databaseCluster).getMetadata();
        // databaseCluster.getSpec().getInstances().get(0).getDataVolumeClaimSpec().getResources().getRequests()
        // .setStorage(instanceDTO.getStorage() + "Gi");
        doReturn(spec).when(databaseCluster).getSpec();

    }
    //
    // @Test
    // void testDeleteCr() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testDeleteAllPod() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testRestartDatabase() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testNodeportSwitch() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testCreateBackup() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testApplyBackupPolicy() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testDeleteBackup() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testApplyConfigParam() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testGetInstanceVOFromCR() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testGetMasterPod() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testRestore() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testGetHgadminPort() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testPatchCrUsers() {
    // fail("Not yet implemented");
    // }

}

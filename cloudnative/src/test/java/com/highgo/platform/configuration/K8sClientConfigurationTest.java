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

package com.highgo.platform.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.highgo.platform.apiserver.model.po.K8sClusterInfoPO;
import com.highgo.platform.apiserver.repository.K8sClusterInfoRepository;
import com.highgo.platform.exception.CommonException;

import io.fabric8.kubernetes.client.KubernetesClient;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class K8sClientConfigurationTest {

    @InjectMocks
    private K8sClientConfiguration k8sClientConfiguration;

    @Mock
    private K8sClusterInfoRepository k8sClusterInfoRepository;

    // k8s config
    private String configInfo = "apiVersion: v1\n" +
            "clusters:\n" +
            "- cluster:\n" +
            "    certificate-authority-data: LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUMvakNDQWVhZ0F3SUJBZ0lCQURBTkJna3Foa2lHOXcwQkFRc0ZBREFWTVJNd0VRWURWUVFERXdwcmRXSmwKY201bGRHVnpNQjRYRFRJek1UQXhNREF5TlRjek0xb1hEVE16TVRBd056QXlOVGN6TTFvd0ZURVRNQkVHQTFVRQpBeE1LYTNWaVpYSnVaWFJsY3pDQ0FTSXdEUVlKS29aSWh2Y05BUUVCQlFBRGdnRVBBRENDQVFvQ2dnRUJBT0pDCkRoanhCL3BHUXkwZnJRRVJ2Zm1YbHpwV0dsWWErdDZkdlIwMi9SSnM4R3VaMmdoeU00VjJQc245UWNXVzc0dmMKR1lsZUljVWxWYzM5MDdCR2Q1alJpbVQrYTM3elNrQ3g4NXV0bmpwenpUbW5EYWg4S3BXTW5JZGIzbU5LZkVJeQpoUDF3amU4ODQ5R0VtRVdnUURNYkFlMVVZWmRvRFFiOTF0bFBTc1NYbW5rcHJrSjVIS0pDY0t2UWtONkxDdEJQCmUrZXlxWitrd1lDU2l3dUljaXVGREplNnpGRlRhT1RMV09ucStlbG12TDAzNXd3NGh2WnR3RHVLdERPT3RaQkoKSlZQSW5hWjF6dFN6ekFTNDNxZU00c29CbkNGU0FneGxGbzRoT1pmekZUbHdPaGx3MUFTVHBLT1Jxek5vdVJuTQphbElYb1RHd3VKVTJ3aHl6bHI4Q0F3RUFBYU5aTUZjd0RnWURWUjBQQVFIL0JBUURBZ0trTUE4R0ExVWRFd0VCCi93UUZNQU1CQWY4d0hRWURWUjBPQkJZRUZNSXg2anpuSWVXUHA1aTVSamQ0UWRHWGkrc1RNQlVHQTFVZEVRUU8KTUF5Q0NtdDFZbVZ5Ym1WMFpYTXdEUVlKS29aSWh2Y05BUUVMQlFBRGdnRUJBRWlXNW1RVVEzRU03dHJYUktQcgpSQXFTdmkwOUZCSmJHdTIwdGdocTNuWm9uOTVyMHJpRVBONUlLN1JoVElGZVZGUEY0cFIxOHR4V3JxY0EzdDhvCkRzT3JWQWViTkxJTVBocUVWMFBJMmxBUlE5RHNKVWk1bk1xS2ZvU2VkVVNrN2NGb1U2UVZOY2JlTDRVb2NjckMKRm1FclVLYVAzbXBSS3hDN3RuTk5nRFpzTVZWY3psK0hIM3ZYWmdGQm5JMHNlNmVGS0Y2WFhCRlJIM2UwamNhUApvY3laSzhhVE1nb0Z2aVJjNkpjQndXeXVvSXJybkFrbWJwMS9TZFBEdVV1UGxBa21FVVduR3ZxQWJVVDRHZW5WCjQ2R1B5WHozR3M5MFdRaVNLalRTamljVVVmTWswY01JTXVud1FlNHlHZUNnSWZsdzlJdFNNTlBSK2tKYWhDWUcKN3YwPQotLS0tLUVORCBDRVJUSUZJQ0FURS0tLS0tCg==\n"
            +
            "    server: https://127.0.1.1:6443\n" +
            "  name: kubernetes\n" +
            "contexts:\n" +
            "- context:\n" +
            "    cluster: kubernetes\n" +
            "    user: kubernetes-admin\n" +
            "  name: kubernetes-admin@kubernetes\n" +
            "current-context: kubernetes-admin@kubernetes\n" +
            "kind: Config\n" +
            "preferences: {}\n" +
            "users:\n" +
            "- name: kubernetes-admin\n" +
            "  user: ";

    @Test
    void testGetAdminKubernetesClientById() {

        // 1. 测试取不到cluster
        CommonException exception = assertThrows(CommonException.class, () -> {
            // 在这里调用该方法
            k8sClientConfiguration.getAdminKubernetesClientById("123456");
        });

        // 可以对异常对象进行进一步的断言
        assertEquals("instance..nternal.error", exception.getMessage());

        // 2. 测试取到cluster
        Mockito.when(k8sClusterInfoRepository.findByClusterId("1")).thenReturn(getK8sList());

        KubernetesClient client = k8sClientConfiguration.getAdminKubernetesClientById("1");
        assertEquals("https://127.0.1.1:6443", client.getMasterUrl().toString());

    }

    @Test
    void testGetAdminKubernetesClientByConfig() {
        KubernetesClient client = k8sClientConfiguration.getAdminKubernetesClientByConfig(configInfo);
        assertEquals("https://127.0.1.1:6443", client.getMasterUrl().toString());
    }

    private K8sClusterInfoPO getK8sClusterInfo() {

        K8sClusterInfoPO cluster = new K8sClusterInfoPO();
        cluster.setClusterId("1");
        cluster.setClusterName("test");;
        cluster.setId("1");
        cluster.setConfig(configInfo);

        return cluster;
    }

    private Optional<K8sClusterInfoPO> getK8sList() {
        K8sClusterInfoPO cluster = getK8sClusterInfo();
        Optional<K8sClusterInfoPO> optionalCluster = Optional.of(cluster);

        return optionalCluster;
    }
}

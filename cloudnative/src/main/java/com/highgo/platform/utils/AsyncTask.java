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

package com.highgo.platform.utils;

import com.highgo.cloud.auth.entity.User;
import com.highgo.cloud.auth.model.dto.MonitorUserDto;
import com.highgo.cloud.auth.repository.AccountRepository;
import com.highgo.cloud.constant.OperatorConstant;
import com.highgo.cloud.enums.*;
import com.highgo.cloud.model.K8sClusterInfoDTO;
import com.highgo.cloud.model.ServerConnectVO;
import com.highgo.cloud.util.BeanUtil;
import com.highgo.cloud.util.SshUtil;
import com.highgo.platform.apiserver.model.dto.AutoScalingHistoryDTO;
import com.highgo.platform.apiserver.model.dto.InstanceDTO;
import com.highgo.platform.apiserver.model.po.K8sClusterInfoPO;
import com.highgo.platform.apiserver.repository.K8sClusterInfoRepository;
import com.highgo.platform.apiserver.service.AlertAutoScalingService;
import com.highgo.platform.apiserver.service.InstanceService;
import com.highgo.platform.apiserver.service.K8sClusterService;
import com.highgo.platform.apiserver.service.MonitorService;
import com.highgo.platform.configuration.K8sClientConfiguration;
import com.highgo.platform.operator.cr.DatabaseCluster;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: highgo-lucunqiao
 * @date: 2023/3/16 18:52
 * @Description: 异步方法
 */
@Component("asyncTask")
@EnableAsync
public class AsyncTask {

    private static final Logger logger = LoggerFactory.getLogger(AsyncTask.class);

    @Autowired
    private K8sClientConfiguration k8sClientConfiguration;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private K8sClusterInfoRepository k8sClusterInfoRepository;

    @Autowired
    @Lazy
    private MonitorService monitorService;

    @Value("${server.port: 8080}")
    private Integer serverPort;

    @Value("${common.serviceName}")
    private String databaseName;

    @Value("${cluster.prometheusName}")
    private String prometheusName;

    @Value("${cluster.grafanaName}")
    private String grafanaName;

    @Value("${cluster.alertManagerName}")
    private String alertManagerName;
    @javax.annotation.Resource(name = "k8sClusterService")
    private K8sClusterService k8sClusterService;

    @javax.annotation.Resource
    @Lazy
    private AlertAutoScalingService alertAutoScalingService;

    @javax.annotation.Resource
    private InstanceService instanceService;

    @Async("monitorExecutor")
    public void createMonitor(MonitorUserDto userDto) {
        // 查询k8s 集群信息
        K8sClusterInfoPO k8sInfo = k8sClusterInfoRepository.findByClusterId(userDto.getClusterId()).get();
        K8sClusterInfoDTO k = new K8sClusterInfoDTO();
        BeanUtil.copyNotNullProperties(k8sInfo, k);
        ServerConnectVO server = SshUtil.getServerConnectVO(k);

        try {
            // 1.创建namespace username+uid
            k8sClusterService.createNamespace(k.getClusterId(), userDto.getNamespace());

            // 2.创建用户的监控安装包空间
            String userDir = "/opt/" + databaseName + "/" + userDto.getNamespace();
            server.setCommand("mkdir -p " + userDir);
            SshUtil.remoteExeCommand(server);
            // 3.将监控安装包从jar包中拷贝到jar包的同级目录
            SshUtil.copyDirFilesFromJar(this.getClass(), "monitor/" + databaseName);

            // 4.压缩监控安装包
            String currentDirPath = SshUtil.getCurrentDirPath();
            // 监控安装包路径
            SshUtil.localExecShell("cd " + currentDirPath);
            SshUtil.localExecShell("tar -cvf " + "./monitor/" + databaseName +".tar " + "./monitor/" + databaseName);

            // 5.将监控安装包上传到k8s master
            String filepath = currentDirPath + "/monitor" + File.separator + databaseName + ".tar";
            SshUtil.uploadFile(server, filepath, userDir);

            // 6.解压监控安装包并执行脚本，根据模板生成用户监控安装文件
            String serverUrl = SshUtil.getLocalIp() + ":" + serverPort;
            server.setCommand("cd " + userDir + " " +
                    "&& tar -xvf " + databaseName + ".tar" +
                    "&& cd monitor/" + databaseName +
                    "&& chmod +x installMonitor.sh " +
                    "&& ./installMonitor.sh " + userDto.getNamespace() + " " + userDto.getAccessMode() + " "
                    + serverUrl);
            SshUtil.remoteExeCommand(server);

            // 7.安装monitor
            server.setCommand("kubectl apply -k " + userDir + "/monitor/" + databaseName);
            String result = SshUtil.remoteExeCommand(server);
            logger.info(result);

            // 8.监控安装listener，回调处理监控安装结果
            Map<String, String> labelFilterMap = new HashMap<>();
            labelFilterMap.put(OperatorConstant.MONITOR_LABEL_KEY, OperatorConstant.MONITOR_LABEL_VALUE);

            LocalDateTime beginWatchTime = LocalDateTime.now();
            KubernetesClient kubernetesClient = k8sClientConfiguration.getAdminKubernetesClientById(k.getClusterId());
            watch: while (true) {
                LocalDateTime currentWatchTime = LocalDateTime.now();
                Duration.between(beginWatchTime, currentWatchTime).toMinutes();
                if (Duration.between(beginWatchTime, currentWatchTime).toMinutes() > 10) {
                    // 部署监控超时，创建失败
                    logger.info("Monitor create failed.");
                    monitorService.createMonitorCallback(userDto, k.getServerUrl(), 0,
                            MonitorStatus.CREATE_FAILED.name());
                    return;
                }
                List<Deployment> items = kubernetesClient.apps().deployments().inNamespace(userDto.getNamespace())
                        .withLabels(labelFilterMap).list().getItems();
                for (Deployment d : items) {
                    Integer availableReplicas = d.getStatus().getAvailableReplicas();
                    String name = d.getMetadata().getName();
                    if (availableReplicas == null) {
                        logger.info("The monitor service {} is not ready", name);
                        Thread.sleep(1000);
                        continue watch;
                    }
                    if (prometheusName.equals(name)) {
                        logger.info("The prometheus is running.");
                        monitorService.createPrometheusCallback(userDto, true);
                    } else if (grafanaName.equals(name)) {
                        logger.info("The grafana is running.");
                        Integer nodePort = kubernetesClient
                                .services()
                                .inNamespace(userDto.getNamespace())
                                .withName(grafanaName)
                                .get()
                                .getSpec()
                                .getPorts()
                                .get(0)
                                .getNodePort();
                        monitorService.createGrafanaCallback(userDto, true);
                        User user = accountRepository.listByUserIdAndClusterId(userDto.getId(), userDto.getClusterId())
                                .get(0);
                        if (user.getPrometheusReady() && user.getGrafanaReady()) {
                            logger.info("Monitor is running.");
                            monitorService.createMonitorCallback(userDto, k.getServerUrl(), nodePort,
                                    MonitorStatus.RUNNING.name());
                            alertAutoScalingService.initAutoScalingAlertRule(String.valueOf(userDto.getId()),
                                    userDto.getClusterId());
                            alertAutoScalingService.initAutoScalingSwitch(String.valueOf(userDto.getId()),
                                    userDto.getClusterId());
                            return;
                        }
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(
                    "[AsyncTask.createMonitor] Failed to create a monitor. [Exception.Message] " + e.getMessage(), e);
        }
    }

    @Async("autoScalingExecutor")
    public void alertAutoScaling(AutoScalingHistoryDTO autoScalingHistoryDTO) {
        // 获取告警的资源类型： cpu memory
        AutoScalingType autoScalingType = autoScalingHistoryDTO.getType();
        InstanceDTO instanceDTO = instanceService.getDTO(autoScalingHistoryDTO.getInstanceId());

        // 整体思想：快扩慢缩
        switch (autoScalingType) {
            case CPU:
                // cpu告警
                logger.warn(
                        "cpu alert,trigger automatic auto scaling of cpu：" + autoScalingHistoryDTO.getAlertMessage());
                CpuAutoScalingHandler(autoScalingHistoryDTO, instanceDTO);
                break;
            case MEMORY:
                // 内存告警
                logger.warn("memory alert,trigger automatic auto scaling of cpu："
                        + autoScalingHistoryDTO.getAlertMessage());
                MemoryAutoScalingHandler(autoScalingHistoryDTO, instanceDTO);
                break;
            default:
                // 其他告警
                logger.warn("other alert type,not support auto scaling.");
                break;
        }
    }

    /**
     * description: memory告警自动扩缩容处理
     * date: 2023/4/23 17:34
     * @param autoScalingHistoryDTO
     * @param instanceDTO
     * @return: void
     * @author: highgo-lucunqiao
     * @since JDK 1.8
     */
    private void MemoryAutoScalingHandler(AutoScalingHistoryDTO autoScalingHistoryDTO, InstanceDTO instanceDTO) {
        AutoScalingOperation operation = autoScalingHistoryDTO.getOperation();
        Integer sourceMemory = instanceDTO.getMemory();

        KubernetesClient kubernetesClient =
                k8sClientConfiguration.getAdminKubernetesClientById(instanceDTO.getClusterId());
        Resource<DatabaseCluster> clusterResource = kubernetesClient.customResources(DatabaseCluster.class)
                .inNamespace(instanceDTO.getNamespace()).withName(instanceDTO.getName());
        DatabaseCluster databaseCluster = clusterResource.get();
        Integer targetMemory = 1;
        if (AutoScalingOperation.SHRINK.equals(operation)) {
            if (sourceMemory <= 1) {
                logger.warn("memory is less than 1,can not auto scaling");
                alertAutoScalingService.autoScalingCallBack(autoScalingHistoryDTO.getId(), AutoScalingStatus.NOTPROCESS,
                        "memory is less than 1,can not auto scaling");
                return;
            }
            targetMemory = sourceMemory - (int) Math.ceil(sourceMemory * 0.3);
        }

        if (AutoScalingOperation.EXPANSION.equals(operation)) {
            targetMemory = sourceMemory + (int) Math.ceil(sourceMemory * 0.8);
        }
        instanceService.updateInstanceMemoryResource(instanceDTO.getId(), targetMemory);
        instanceService.updateInstanceStatus(instanceDTO.getId(), InstanceStatus.AUTO_SCALING);
        alertAutoScalingService.updateResourceValue(autoScalingHistoryDTO.getId(), String.valueOf(sourceMemory),
                String.valueOf(targetMemory));
        alertAutoScalingService.updateAutoScalingStatus(autoScalingHistoryDTO.getId(), AutoScalingStatus.PROCESSING);
        databaseCluster.getMetadata().getLabels().put(OperatorConstant.OPERATE_LABEL,
                InstanceStatus.AUTO_SCALING.name());
        databaseCluster.getSpec().getInstances().get(0).getResources().getLimits().setMemory(targetMemory + "Gi");
        clusterResource.patch(databaseCluster);

    }

    /**
     * description: cpu告警自动扩缩容处理
     * date: 2023/4/23 17:34
     * @param autoScalingHistoryDTO
     * @param instanceDTO
     * @return: void
     * @author: highgo-lucunqiao
     * @since JDK 1.8
     */
    private void CpuAutoScalingHandler(AutoScalingHistoryDTO autoScalingHistoryDTO, InstanceDTO instanceDTO) {
        AutoScalingOperation operation = autoScalingHistoryDTO.getOperation();
        Integer sourceCpu = instanceDTO.getCpu();

        KubernetesClient kubernetesClient =
                k8sClientConfiguration.getAdminKubernetesClientById(instanceDTO.getClusterId());
        Resource<DatabaseCluster> clusterResource = kubernetesClient.customResources(DatabaseCluster.class)
                .inNamespace(instanceDTO.getNamespace()).withName(instanceDTO.getName());
        DatabaseCluster databaseCluster = clusterResource.get();
        Integer targetCpu = 1;
        if (AutoScalingOperation.SHRINK.equals(operation)) {
            // 缩容
            if (sourceCpu <= 1) {
                logger.warn("cpu is less than 1,can not auto scaling");
                alertAutoScalingService.autoScalingCallBack(autoScalingHistoryDTO.getId(), AutoScalingStatus.NOTPROCESS,
                        "cpu is less than 1,can not auto scaling");
                return;
            }
            targetCpu = sourceCpu - (int) Math.ceil(sourceCpu * 0.3);
        }

        if (AutoScalingOperation.EXPANSION.equals(operation)) {
            // 扩容
            targetCpu = sourceCpu + (int) Math.ceil(sourceCpu * 0.8);
        }
        instanceService.updateInstanceCpuResource(instanceDTO.getId(), targetCpu);
        instanceService.updateInstanceStatus(instanceDTO.getId(), InstanceStatus.AUTO_SCALING);
        alertAutoScalingService.updateResourceValue(autoScalingHistoryDTO.getId(), String.valueOf(sourceCpu),
                String.valueOf(targetCpu));
        alertAutoScalingService.updateAutoScalingStatus(autoScalingHistoryDTO.getId(), AutoScalingStatus.PROCESSING);
        databaseCluster.getMetadata().getLabels().put(OperatorConstant.OPERATE_LABEL,
                InstanceStatus.AUTO_SCALING.name());
        databaseCluster.getSpec().getInstances().get(0).getResources().getLimits().setCpu(String.valueOf(targetCpu));
        clusterResource.patch(databaseCluster);

    }

}

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.highgo.cloud.auth.entity.User;
import com.highgo.cloud.auth.model.dto.MonitorUserDto;
import com.highgo.cloud.auth.repository.AccountRepository;
import com.highgo.cloud.constant.InstanceAllowConstant;
import com.highgo.cloud.constant.OperatorConstant;
import com.highgo.cloud.enums.AutoScalingOperation;
import com.highgo.cloud.enums.AutoScalingStatus;
import com.highgo.cloud.enums.AutoScalingType;
import com.highgo.cloud.enums.SwitchStatus;
import com.highgo.cloud.model.K8sClusterInfoDTO;
import com.highgo.cloud.model.ServerConnectVO;
import com.highgo.cloud.util.BeanUtil;
import com.highgo.cloud.util.CommonUtil;
import com.highgo.cloud.util.SshUtil;
import com.highgo.platform.apiserver.model.dto.AutoScalingAlertRuleDTO;
import com.highgo.platform.apiserver.model.dto.AutoScalingHistoryDTO;
import com.highgo.platform.apiserver.model.dto.AutoScalingSwitchDTO;
import com.highgo.platform.apiserver.model.dto.InstanceDTO;
import com.highgo.platform.apiserver.model.po.AutoScalingAlertRulePO;
import com.highgo.platform.apiserver.model.po.AutoScalingHistoryPO;
import com.highgo.platform.apiserver.model.po.AutoScalingSwitchPO;
import com.highgo.platform.apiserver.model.po.K8sClusterInfoPO;
import com.highgo.platform.apiserver.model.vo.request.AlertMessage;
import com.highgo.platform.apiserver.model.vo.request.ModifyAlertRuleVO;
import com.highgo.platform.apiserver.model.vo.request.ModifySwitchVO;
import com.highgo.platform.apiserver.model.vo.response.ActionResponse;
import com.highgo.platform.apiserver.model.vo.response.AutoScalingAlertRuleVO;
import com.highgo.platform.apiserver.model.vo.response.AutoScalingSwitchVO;
import com.highgo.platform.apiserver.repository.AutoScalingAlertRuleRepository;
import com.highgo.platform.apiserver.repository.AutoScalingHistoryRepository;
import com.highgo.platform.apiserver.repository.AutoScalingSwitchRepository;
import com.highgo.platform.apiserver.service.AlertAutoScalingService;
import com.highgo.platform.apiserver.service.InstanceService;
import com.highgo.platform.apiserver.service.K8sClusterService;
import com.highgo.platform.configuration.K8sClientConfiguration;
import com.highgo.platform.errorcode.AutoScalingError;
import com.highgo.platform.exception.AutoScalingException;
import com.highgo.platform.utils.AsyncTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author: highgo-lucunqiao
 * @date: 2023/4/19 10:32
 * @Description: 自动弹性伸缩service实现类
 */
@Service
@Slf4j
public class AlertAutoScalingServiceImpl implements AlertAutoScalingService {

    @Autowired
    private InstanceService instanceService;

    @Autowired
    private AutoScalingHistoryRepository autoScalingHistoryRepository;

    @Autowired
    private AutoScalingSwitchRepository autoScalingSwitchRepository;

    @Autowired
    private AutoScalingAlertRuleRepository autoScalingAlertRuleRepository;

    @Autowired
    private K8sClusterService k8sClusterService;

    @Resource(name = "asyncTask")
    private AsyncTask asyncTask;

    @Value("${common.serviceName}")
    private String databaseName;
    @Resource
    private AccountRepository accountRepository;

    @Autowired
    K8sClientConfiguration k8sClientConfiguration;

    @Value("${cluster.prometheusName}")
    private String prometheusName;
    @Override
    @Transactional
    public void alertAutoScaling(AlertMessage alertMessage) {

        List<AlertMessage.Alerts> alerts = alertMessage.getAlerts();
        alerts = alerts
                .stream()
                .filter(a -> a.getStatus().equals("firing"))
                .collect(Collectors.toList());

        for (AlertMessage.Alerts a : alerts) {
            String instanceId = a.getLabels().getInstanceId();
            InstanceDTO instance = instanceService.getDTO(instanceId);

            // 1.判断自动弹性伸缩开关是否开启
            AutoScalingSwitchPO autoScalingswitch = autoScalingSwitchRepository
                    .findByUserIdAndClusterIdAndIsDeleted(instance.getCreator(), instance.getClusterId(), false);
            if (autoScalingswitch == null || SwitchStatus.OFF.equals(autoScalingswitch.getAutoscalingSwitch())) {
                log.warn(
                        "[AlertAutoScalingServiceImpl.alertAutoScaling] Not processed,autoscaling switch is off,instanceId: {}, cluterId: {}, userId: {}",
                        instanceId, instance.getClusterId(), instance.getCreator());
                return;
            }

            // 2.检查弹性伸缩处理时间
            if (!checkInstanceAutoScaling(instanceId, a)) {
                return;
            }

            // 3.本次告警信息log入库
            AutoScalingHistoryDTO autoScalingHistoryDTO = getAutoScalingHistoryDTOFromAlertMessage(a);
            AutoScalingHistoryPO autoScalingHistoryPO = new AutoScalingHistoryPO();
            BeanUtil.copyNotNullProperties(autoScalingHistoryDTO, autoScalingHistoryPO);
            autoScalingHistoryPO = autoScalingHistoryRepository.save(autoScalingHistoryPO);
            BeanUtil.copyNotNullProperties(autoScalingHistoryPO, autoScalingHistoryDTO);

            // 4.告警信息处理
            if (InstanceAllowConstant.ALLOW_AUTOSCALING_STATUS.contains(instance.getStatus())) {
                // 自动弹性伸缩异步处理
                asyncTask.alertAutoScaling(autoScalingHistoryDTO);
            } else {
                // 不允许自动伸缩的状态，不处理
                autoScalingCallBack(autoScalingHistoryDTO.getId(), AutoScalingStatus.NOTPROCESS,
                        "Not processed,instance status is not allow autoscaling,status: " + instance.getStatus());
                log.warn(
                        "[AlertAutoScalingServiceImpl.alertAutoScaling] Not processed,instance status is not allow autoscaling,instanceId: {},status: {}",
                        instanceId, instance.getStatus());
            }

        }
    }

    /**
     * description: 检查实例5分钟内是否收到了相同的扩缩容请求
     * date: 2023/4/25 14:15
     * @param instanceId
     * @param a
     * @return: boolean
     * @author: highgo-lucunqiao
     * @since JDK 1.8
     */
    private boolean checkInstanceAutoScaling(String instanceId, AlertMessage.Alerts a) {
        AlertMessage.Alerts.Labels labels = a.getLabels();
        List<AutoScalingHistoryPO> autoScalingHistoryPOS = autoScalingHistoryRepository
                .findByInstanceIdAndSameAutoScaling(instanceId, AutoScalingType.valueOf(labels.getType().toUpperCase()),
                        AutoScalingOperation.valueOf(labels.getAutoscaling().toUpperCase()), PageRequest.of(0, 1));
        if (CollectionUtils.isEmpty(autoScalingHistoryPOS)) {
            // 没有历史记录，允许处理
            return true;
        }
        AutoScalingHistoryPO autoScalingHistoryPO = autoScalingHistoryPOS.get(0);
        AutoScalingHistoryDTO autoScalingHistoryDTO = new AutoScalingHistoryDTO();
        BeanUtil.copyNotNullProperties(autoScalingHistoryPO, autoScalingHistoryDTO);
        Date createdAt = autoScalingHistoryDTO.getCreatedAt();
        Date utcDate = CommonUtil.getUTCDate();

        if (utcDate.getTime() - createdAt.getTime() < TimeUnit.MINUTES.toMillis(2)) {
            // 2分钟内不允许重复处理
            log.warn(
                    "[AlertAutoScalingServiceImpl.alertAutoScaling] Not processed,The instance received the same autoscaling request within 5 minutes,instanceId: {}",
                    instanceId);
            return false;
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void autoScalingCallBack(String autoScalingHistoryId, AutoScalingStatus status, String operatorLog) {
        autoScalingHistoryRepository.findById(autoScalingHistoryId).ifPresent(autoScalingHistory -> {
            autoScalingHistory.setStatus(status);
            autoScalingHistory.setOperateLog(operatorLog);
            autoScalingHistory.setUpdatedAt(CommonUtil.getUTCDate());
            autoScalingHistoryRepository.save(autoScalingHistory);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void autoScalingOperatorCallBack(String instanceId, AutoScalingStatus status, String operatorLog) {
        List<AutoScalingHistoryPO> autoScalingHistoryPOS = autoScalingHistoryRepository
                .findByInstanceIdAndStatusOrderByCreatedAtDesc(instanceId, AutoScalingStatus.PROCESSING);
        if (!CollectionUtils.isEmpty(autoScalingHistoryPOS)) {
            autoScalingHistoryPOS.forEach(autoScalingHistoryPO -> {
                autoScalingHistoryPO.setStatus(status);
                autoScalingHistoryPO.setUpdatedAt(CommonUtil.getUTCDate());
                autoScalingHistoryPO.setOperateLog(operatorLog);
                autoScalingHistoryRepository.save(autoScalingHistoryPO);
            });

            autoScalingHistoryRepository.saveAll(autoScalingHistoryPOS);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAutoScalingStatus(String autoScalingHistoryId, AutoScalingStatus status) {
        autoScalingHistoryRepository.findById(autoScalingHistoryId).ifPresent(autoScalingHistory -> {
            autoScalingHistory.setStatus(status);
            autoScalingHistory.setUpdatedAt(CommonUtil.getUTCDate());
            autoScalingHistoryRepository.save(autoScalingHistory);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateResourceValue(String autoScalingHistoryId, String sourceValue, String targetValue) {
        autoScalingHistoryRepository.findById(autoScalingHistoryId).ifPresent(autoScalingHistory -> {
            autoScalingHistory.setSourceValue(sourceValue);
            autoScalingHistory.setTargetValue(targetValue);
            autoScalingHistory.setUpdatedAt(CommonUtil.getUTCDate());
            autoScalingHistoryRepository.save(autoScalingHistory);
        });
    }

    @Override
    public AutoScalingSwitchDTO getAutoScalingSwitchDTO(String userId, String clusterId) {
        AutoScalingSwitchPO autoScalingSwitchPO =
                autoScalingSwitchRepository.findByUserIdAndClusterIdAndIsDeleted(userId, clusterId, false);
        if (autoScalingSwitchPO == null) {
            throw new AutoScalingException(AutoScalingError.AUTOSCALINGSWITCH_NOT_EXIST);
        }
        AutoScalingSwitchDTO autoScalingSwitchDTO = new AutoScalingSwitchDTO();
        BeanUtil.copyNotNullProperties(autoScalingSwitchPO, autoScalingSwitchDTO);
        return autoScalingSwitchDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ActionResponse autoscalingSwitch(String userId, String clusterId, ModifySwitchVO modifySwitchVO) {

        checkAlertRule(userId, clusterId, modifySwitchVO.getSwitchStatus());

        AutoScalingSwitchDTO autoScalingSwitchDTO = getAutoScalingSwitchDTO(userId, clusterId);
        autoScalingSwitchDTO.setAutoscalingSwitch(modifySwitchVO.getSwitchStatus());
        AutoScalingSwitchPO autoScalingSwitchPO = new AutoScalingSwitchPO();
        BeanUtil.copyNotNullProperties(autoScalingSwitchDTO, autoScalingSwitchPO);
        autoScalingSwitchRepository.save(autoScalingSwitchPO);
        return ActionResponse.actionSuccess();
    }

    private void checkAlertRule(String userId, String clusterId, SwitchStatus switchStatus) {

        if (SwitchStatus.OFF.equals(switchStatus)) {
            // 关闭自动弹性伸缩开关，不校验告警规则
            return;
        }
        List<AutoScalingAlertRulePO> autoScalingAlertRulePOList =
                autoScalingAlertRuleRepository.findByClusterIdAndUserIdAndIsDeleted(clusterId, userId, false);
    }

    @Override
    public AutoScalingSwitchVO getAutoScalingSwitch(String userId, String clusterId) {
        AutoScalingSwitchDTO autoScalingSwitchDTO = getAutoScalingSwitchDTO(userId, clusterId);
        AutoScalingSwitchVO autoScalingSwitchVO = new AutoScalingSwitchVO();
        BeanUtil.copyNotNullProperties(autoScalingSwitchDTO, autoScalingSwitchVO);
        return autoScalingSwitchVO;
    }

    @Override
    public List<AutoScalingAlertRuleVO> getAutoScalingAlertRule(String userId, String clusterId) {
        List<AutoScalingAlertRulePO> autoScalingAlertRulePOList =
                autoScalingAlertRuleRepository.findByClusterIdAndUserIdAndIsDeleted(clusterId, userId, false);
        List<AutoScalingAlertRuleVO> autoScalingAlertRuleVOList = new ArrayList<>();
        for (AutoScalingAlertRulePO autoScalingAlertRulePO : autoScalingAlertRulePOList) {
            AutoScalingAlertRuleVO autoScalingAlertRuleVO = new AutoScalingAlertRuleVO();
            BeanUtil.copyNotNullProperties(autoScalingAlertRulePO, autoScalingAlertRuleVO);
            autoScalingAlertRuleVOList.add(autoScalingAlertRuleVO);
        }
        return autoScalingAlertRuleVOList;
    }

    @Override
    public void initAutoScalingAlertRule(String userId, String clusterId) {

        List<AutoScalingAlertRulePO> autoScalingAlertRulePOList = new ArrayList<>();
        // 解析xml，生成规则
        parseRuleXml(autoScalingAlertRulePOList);
        Date utcDate = CommonUtil.getUTCDate();
        autoScalingAlertRulePOList.forEach(
                autoScalingAlertRulePO -> {
                    autoScalingAlertRulePO.setUserId(userId);
                    autoScalingAlertRulePO.setClusterId(clusterId);
                    autoScalingAlertRulePO.setCreatedAt(utcDate);
                });
        autoScalingAlertRuleRepository.saveAll(autoScalingAlertRulePOList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<AutoScalingAlertRuleVO> AutoScalingAlertRule(List<ModifyAlertRuleVO> modifyAlertRuleVOs) {
        List<AutoScalingAlertRuleDTO> autoScalingAlertRuleDTOList = new ArrayList<>();
        for (ModifyAlertRuleVO m : modifyAlertRuleVOs) {
            Optional<AutoScalingAlertRulePO> autoScalingAlertRulePOOptional =
                    autoScalingAlertRuleRepository.findById(m.getId());
            if (autoScalingAlertRulePOOptional.isPresent()) {
                AutoScalingAlertRulePO autoScalingAlertRulePO = autoScalingAlertRulePOOptional.get();
                autoScalingAlertRulePO.setThreshold(m.getThreshold());
                autoScalingAlertRulePO.setDuration(m.getDuration());
                autoScalingAlertRulePO.setUpdatedAt(CommonUtil.getUTCDate());
                AutoScalingAlertRulePO save = autoScalingAlertRuleRepository.save(autoScalingAlertRulePO);
                AutoScalingAlertRuleDTO autoScalingAlertRuleDTO = new AutoScalingAlertRuleDTO();
                BeanUtil.copyNotNullProperties(save, autoScalingAlertRuleDTO);
                autoScalingAlertRuleDTOList.add(autoScalingAlertRuleDTO);
            }
        }
        // 修改alertmanager规则
        configAlertManagerRule(autoScalingAlertRuleDTOList);

        return autoScalingAlertRuleDTOList
                .stream()
                .map(autoScalingAlertRuleDTO -> {
                    AutoScalingAlertRuleVO autoScalingAlertRuleVO = new AutoScalingAlertRuleVO();
                    BeanUtil.copyNotNullProperties(autoScalingAlertRuleDTO, autoScalingAlertRuleVO);
                    return autoScalingAlertRuleVO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void initAutoScalingSwitch(String userId, String clusterId) {
        AutoScalingSwitchPO autoScalingSwitchPO = new AutoScalingSwitchPO();
        autoScalingSwitchPO.setUserId(userId);
        autoScalingSwitchPO.setClusterId(clusterId);
        autoScalingSwitchPO.setAutoscalingSwitch(SwitchStatus.OFF);
        autoScalingSwitchPO.setCreatedAt(CommonUtil.getUTCDate());
        autoScalingSwitchRepository.save(autoScalingSwitchPO);
    }

    /**
     * description: 修改alertmanager规则
     * date: 2023/4/24 13:29
     * @param autoScalingAlertRuleDTOList
     * @return: void
     * @author: highgo-lucunqiao
     * @since JDK 1.8
     */
    private void configAlertManagerRule(List<AutoScalingAlertRuleDTO> autoScalingAlertRuleDTOList) {
        try {
            // 1.获取集群master连接信息
            AutoScalingAlertRuleDTO autoScalingAlertRuleDTO = autoScalingAlertRuleDTOList.get(0);
            String clusterId = autoScalingAlertRuleDTO.getClusterId();
            K8sClusterInfoPO k8sClusterInfoPO = k8sClusterService.getInfoByClusterId(clusterId);
            K8sClusterInfoDTO k8sClusterInfoDTO = new K8sClusterInfoDTO();
            BeanUtil.copyNotNullProperties(k8sClusterInfoPO, k8sClusterInfoDTO);
            ServerConnectVO server = SshUtil.getServerConnectVO(k8sClusterInfoDTO);
            // 2.生成需要修改的告警规则 如： CPUHigh*90*60 MemoryLow*50*60
            List<String> ruleStrList = autoScalingAlertRuleDTOList
                    .stream()
                    .map(a -> a.getAlertName() + "*" + a.getThreshold() + "*" + a.getDuration())
                    .collect(Collectors.toList());

            // 3.调用脚本，修改用户目录下的alertmanager-rules-config.yaml文件中，告警规则
            int userId = autoScalingAlertRuleDTO.getUserId();
            List<User> users = accountRepository.listByUserIdAndClusterId(userId, clusterId);
            MonitorUserDto userDTO = new MonitorUserDto();
            if (CollectionUtils.isEmpty(users)) {
                throw new AutoScalingException(AutoScalingError.USER_MONITOR_NOT_EXIST);
            }
            BeanUtil.copyNotNullProperties(users.get(0), userDTO);
            String userDir = "/opt/" + databaseName + "/" + userDTO.getNamespace();
            server.setCommand("cd " + userDir
                    + " && chmod +x ./monitor/"+ databaseName + "/configAlertRule.sh "
                    + " && ./monitor/" + databaseName + "/configAlertRule.sh " + String.join(" ", ruleStrList)
                    + " && kubectl apply -k ./monitor/" + databaseName);
            String result = SshUtil.remoteExeCommand(server);
            log.info("configAlertManagerRule result: {}", result);

            // 4.删除alertmanager pod 重新读取规则
            Map<String, String> labelFilterMap = new HashMap<>();
            labelFilterMap.put(OperatorConstant.MONITOR_LABEL_KEY, OperatorConstant.MONITOR_LABEL_VALUE);
            labelFilterMap.put(OperatorConstant.MONITOR_NAME_LABEL, prometheusName);
            k8sClientConfiguration.getAdminKubernetesClientById(clusterId).pods().inNamespace(userDTO.getNamespace())
                    .withLabels(labelFilterMap).delete();
        } catch (Exception e) {
            throw new AutoScalingException(AutoScalingError.CONFIG_ALERTMANAGER_FAILED);
        }
    }

    /**
     * description: 解析xml，生成autoscaling alert规则
     * date: 2023/4/23 17:28
     * @param autoScalingAlertRulePOList
     * @return: List<AutoScalingAlertRulePO>
     * @author: highgo-lucunqiao
     * @since JDK 1.8
     */
    private List<AutoScalingAlertRulePO> parseRuleXml(List<AutoScalingAlertRulePO> autoScalingAlertRulePOList) {
        try {
            SshUtil.copyDirFilesFromJar(this.getClass(), "monitor/ivory/alert-rule.xml");
            // 创建DocumentBuilderFactory对象
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // 创建DocumentBuilder对象
            DocumentBuilder builder = factory.newDocumentBuilder();
            // 解析XML文件，获取Document对象
            Document document = builder.parse("monitor/ivory/alert-rule.xml");
            // 获取根元素
            Element root = document.getDocumentElement();
            // 遍历子元素
            NodeList firstChildNodes = root.getChildNodes();

            for (int i = 0; i < firstChildNodes.getLength(); i++) {
                if (firstChildNodes.item(i) instanceof Element) {
                    // 获取子元素
                    Element child = (Element) firstChildNodes.item(i);
                    // 获取子元素属性和值
                    log.info("init alert rule name: {}, id: {}", child.getAttribute("name"), child.getAttribute("id"));
                    NodeList childNodes = child.getChildNodes();
                    StringBuilder sb = new StringBuilder("{");
                    for (int j = 0; j < childNodes.getLength(); j++) {
                        if (childNodes.item(j) instanceof Element) {
                            Element c = (Element) childNodes.item(j);
                            sb.append("\"" + c.getNodeName() + "\"");
                            sb.append(": ");
                            sb.append("\"" + c.getAttribute("value") + "\"");
                            sb.append(",");
                        }
                    }
                    sb.deleteCharAt(sb.lastIndexOf(","));
                    sb.append("}");
                    log.info("init alert rule: {}", sb);
                    ObjectMapper objectMapper = new ObjectMapper();
                    AutoScalingAlertRulePO rule = objectMapper.readValue(sb.toString(), AutoScalingAlertRulePO.class);
                    rule.setAlertName(child.getAttribute("name"));
                    autoScalingAlertRulePOList.add(rule);
                }
            }
        } catch (Exception e) {
            log.error("[AlertAutoScalingServiceImpl.parseRuleXml]解析alert-rule.xml文件失败", e);
        }
        return autoScalingAlertRulePOList;
    }

    /**
     * description: 从告警信息中获取自动弹性伸缩历史记录DTO
     * date: 2023/4/20 10:20
     *
     * @param alert
     * @return: AutoScalingHistoryDTO
     * @author: highgo-lucunqiao
     * @since JDK 1.8
     */
    public AutoScalingHistoryDTO getAutoScalingHistoryDTOFromAlertMessage(AlertMessage.Alerts alert) {

        AlertMessage.Alerts.Labels labels = alert.getLabels();

        return AutoScalingHistoryDTO
                .builder()
                .instanceId(labels.getInstanceId())
                .alertMessage(alert.getAnnotations().getSummary())
                .type(AutoScalingType.valueOf(labels.getType().toUpperCase()))
                .operation(AutoScalingOperation.valueOf(labels.getAutoscaling().toUpperCase()))
                .status(AutoScalingStatus.PREPAREPROCESS)
                .createdAt(CommonUtil.getUTCDate())
                .build();
    }

}

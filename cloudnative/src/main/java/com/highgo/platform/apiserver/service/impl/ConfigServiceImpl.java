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

import com.highgo.cloud.constant.InstanceAllowConstant;
import com.highgo.cloud.enums.InstanceStatus;
import com.highgo.cloud.enums.OperationName;
import com.highgo.cloud.enums.OperationStatus;
import com.highgo.cloud.model.PageInfo;
import com.highgo.cloud.util.BeanUtil;
import com.highgo.cloud.util.CommonUtil;
import com.highgo.platform.apiserver.model.dto.ConfigInstanceParamDTO;
import com.highgo.platform.apiserver.model.dto.InstanceDTO;
import com.highgo.platform.apiserver.model.dto.OperationDTO;
import com.highgo.platform.apiserver.model.po.ConfigChangeHistoryPO;
import com.highgo.platform.apiserver.model.po.ConfigChangeParamPO;
import com.highgo.platform.apiserver.model.po.ConfigInstanceParamPO;
import com.highgo.platform.apiserver.model.po.InstancePO;
import com.highgo.platform.apiserver.model.vo.request.ConfigChangeParamVO;
import com.highgo.platform.apiserver.model.vo.request.ModifyConfigChangeVO;
import com.highgo.platform.apiserver.model.vo.response.ActionResponse;
import com.highgo.platform.apiserver.model.vo.response.ConfigChangeHistoryVO;
import com.highgo.platform.apiserver.model.vo.response.ConfigChangeVO;
import com.highgo.platform.apiserver.model.vo.response.ConfigParamInfoVO;
import com.highgo.platform.apiserver.repository.*;
import com.highgo.platform.apiserver.service.ConfigService;
import com.highgo.platform.apiserver.service.InstanceService;
import com.highgo.platform.errorcode.InstanceError;
import com.highgo.platform.exception.InstanceException;
import com.highgo.platform.operator.service.CrService;
import com.highgo.platform.websocket.service.WebsocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ConfigServiceImpl implements ConfigService {

    private static final Logger logger = LoggerFactory.getLogger(ConfigServiceImpl.class);

    @Autowired
    ConfigDefinationRepository configDefinationRepository;

    @Autowired
    ConfigChangeHistoryRepository configChangeHistoryRepository;

    @Autowired
    ConfigChangeParamRepository configChangeParamRepository;

    @Autowired
    ConfigInstanceParamRepository configInstanceParamRepository;

    @Resource
    CrService crService;

    @Autowired
    InstanceService instanceService;

    @Autowired
    InstanceRepository instanceRepository;

    @Autowired
    WebsocketService websocketService;

    /**
     * 参数配置列表
     *
     * @param id
     * @return
     */
    @Override
    public List<ConfigParamInfoVO> listParamters(String id) {

        List<Map> objectList = configDefinationRepository.listParamByInstanceId(id);
        List<ConfigParamInfoVO> configParamInfoVOS = new ArrayList<>();
        for (Map<?, ?> map : objectList) {
            ConfigParamInfoVO instanceParamsInfo = new ConfigParamInfoVO();
            BeanUtil.copyProperties(map, instanceParamsInfo);
            configParamInfoVOS.add(instanceParamsInfo);
        }
        return configParamInfoVOS;
    }

    /**
     * 修改参数配置
     *
     * @param id
     * @param modifyConfigChangeParam
     * @return
     */
    @Override
    public ActionResponse modifyParameters(String id, ModifyConfigChangeVO modifyConfigChangeParam) {
        InstanceDTO instanceDTO = instanceService.beforeOperateInstance(id);
        if (!InstanceAllowConstant.ALLOW_CONFIG_CHANGE_STATUS.contains(instanceDTO.getStatus())) {
            throw new InstanceException(InstanceError.INSTANCE_NOT_ALLOW_OPERATE);
        }
        instanceDTO.setStatus(InstanceStatus.CONFIG_CHANGING);
        // 1 config_change_history增加历史记录
        ConfigChangeHistoryPO configChangeHistoryPO = new ConfigChangeHistoryPO();
        configChangeHistoryPO.setDescription(modifyConfigChangeParam.getDescription());
        configChangeHistoryPO.setInstanceId(id);
        configChangeHistoryPO.setStatus(OperationStatus.PROCESSING);
        configChangeHistoryPO.setCreatedAt(CommonUtil.getUTCDate());
        configChangeHistoryPO = configChangeHistoryRepository.save(configChangeHistoryPO);
        instanceDTO.setConfigChangeHistoryId(configChangeHistoryPO.getId());

        // 2 config_change_param表增加流水记录
        List<ConfigChangeParamPO> configChangeParamPOList = new ArrayList<>(); // 流水表入库
        List<ConfigInstanceParamDTO> configInstanceParamDTOS = new ArrayList<>(); // 提交给cr执行变更
        List<ConfigChangeParamVO> configChangeParamVOS = modifyConfigChangeParam.getParams();
        List<ConfigInstanceParamPO> configInstanceParamPOList = configInstanceParamRepository.listByInstanceId(id);
        for (ConfigChangeParamVO configChangeParamVO : configChangeParamVOS) {
            for (ConfigInstanceParamPO instanceParamPO : configInstanceParamPOList) {
                if (!instanceParamPO.getName().equals(configChangeParamVO.getParamName())) {
                    continue;
                }
                if (instanceParamPO.getValue().equals(configChangeParamVO.getTargetValue())) {
                    // 目标值和当前值一致，不做变更，跳过该条记录
                    continue;
                }
                ConfigInstanceParamDTO configInstanceParamDTO = new ConfigInstanceParamDTO();
                configInstanceParamDTO.setName(configChangeParamVO.getParamName());
                configInstanceParamDTO.setValue(configChangeParamVO.getTargetValue());
                configInstanceParamDTOS.add(configInstanceParamDTO);
                ConfigChangeParamPO configChangeParamPO = new ConfigChangeParamPO();
                configChangeParamPO.setConfigChangeHistoryId(configChangeHistoryPO.getId());
                configChangeParamPO.setParamName(configChangeParamVO.getParamName());
                configChangeParamPO.setTargetValue(configChangeParamVO.getTargetValue());
                configChangeParamPO.setSourceValue(instanceParamPO.getValue());
                configChangeParamPOList.add(configChangeParamPO);
            }
        }
        if (configChangeParamPOList.isEmpty()) {
            // 无变更参数
            throw new InstanceException(InstanceError.INSTANCE_NO_CHANGE);
        }
        configChangeParamRepository.saveAll(configChangeParamPOList);

        // 3 修改实例状态为更配中
        InstancePO instancePO = new InstancePO();
        BeanUtil.copyNotNullProperties(instanceDTO, instancePO);
        instanceRepository.save(instancePO);

        // 4 下发任务到cr执行变更
        instanceDTO.setConfigInstanceParamDTOS(configInstanceParamDTOS);
        crService.applyConfigParam(instanceDTO);
        return ActionResponse.actionSuccess();
    }

    /**
     * 修改参数完成回调方法
     *
     * @param id
     * @param configHistoryId
     * @param result
     */
    @Override
    public void modifyParametersCallback(String id, String configHistoryId, boolean result) {
        Optional<InstancePO> instancePOOptional = instanceRepository.findById(id);
        if (!instancePOOptional.isPresent()) {
            logger.error("[ConfigServiceImpl.modifyParametersCallback] instance is not exist. instance id is {}", id);
            return;
        }
        Optional<ConfigChangeHistoryPO> configChangeHistoryPOOptional =
                configChangeHistoryRepository.findById(configHistoryId);
        if (!configChangeHistoryPOOptional.isPresent()) {
            logger.error(
                    "[ConfigServiceImpl.modifyParametersCallback] ConfigChangeHistoryPO is not exist. ConfigChangeHistoryPO id is {}",
                    configHistoryId);
            return;
        }
        InstancePO instancePO = instancePOOptional.get();
        ConfigChangeHistoryPO configChangeHistoryPO = configChangeHistoryPOOptional.get();
        OperationStatus operationStatus;
        if (result) {
            // 修改成功
            // 将实例状态变更为运行中
            // 将实例参数更新
            instancePO.setStatus(InstanceStatus.RUNNING);
            configChangeHistoryPO.setStatus(OperationStatus.SUCCESS);
            List<ConfigChangeParamPO> configChangeParamPOList =
                    configChangeParamRepository.listConfigChangeParamByHistoryId(configHistoryId);
            List<ConfigInstanceParamPO> configInstanceParamPOList = configInstanceParamRepository.listByInstanceId(id);
            List<ConfigInstanceParamPO> changedConfigInstanceParamPOList = new ArrayList<>();
            for (ConfigChangeParamPO configChangeParamPO : configChangeParamPOList) {
                for (ConfigInstanceParamPO configInstanceParamPO : configInstanceParamPOList) {
                    if (configChangeParamPO.getParamName().equals(configInstanceParamPO.getName())) {
                        configInstanceParamPO.setValue(configChangeParamPO.getTargetValue());
                        changedConfigInstanceParamPOList.add(configInstanceParamPO);
                    }
                }
            }
            configInstanceParamRepository.saveAll(changedConfigInstanceParamPOList);
            operationStatus = OperationStatus.SUCCESS;
        } else {
            // 修改失败
            instancePO.setStatus(InstanceStatus.CONFIG_CHANGE_FAILED);
            configChangeHistoryPO.setStatus(OperationStatus.FAILED);
            operationStatus = OperationStatus.FAILED;
        }
        instancePO.setUpdatedAt(CommonUtil.getUTCDate());
        configChangeHistoryPO.setUpdatedAt(CommonUtil.getUTCDate());
        instanceRepository.save(instancePO);
        configChangeHistoryRepository.save(configChangeHistoryPO);
        InstanceDTO instanceDTO = new InstanceDTO();
        BeanUtil.copyNotNullProperties(instancePO, instanceDTO);
        // String projectId = instanceService.getProjectIdByNamespace(instanceDTO.getClusterId(),
        // instanceDTO.getNamespace());
        websocketService.sendMsgToUser(instanceDTO,
                OperationDTO.builder().name(OperationName.CONFIG_CHANGE).status(operationStatus).build());

    }

    /**
     * 参数配置修改历史记录分页
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo<List<ConfigChangeHistoryVO>> listHistory(String instanceId, int pageNo, int pageSize) {
        Specification<ConfigChangeHistoryPO> specification = new Specification<ConfigChangeHistoryPO>() {

            @Override
            public Predicate toPredicate(Root<ConfigChangeHistoryPO> root, CriteriaQuery<?> query,
                    CriteriaBuilder criteriaBuilder) {
                Predicate deletedPredicate = criteriaBuilder.equal(root.get("isDeleted"), false);
                Predicate instancePredicate = criteriaBuilder.equal(root.get("instanceId"), instanceId);
                Predicate resultPredicate = criteriaBuilder.and(deletedPredicate, instancePredicate);
                query.orderBy(criteriaBuilder.desc(root.get("createdAt")));
                return resultPredicate;
            }
        };

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<ConfigChangeHistoryPO> page = configChangeHistoryRepository.findAll(specification, pageable);
        List<ConfigChangeHistoryVO> configChangeHistoryVOS = new ArrayList<>();
        for (ConfigChangeHistoryPO configChangeHistoryPO : page.getContent()) {
            ConfigChangeHistoryVO configChangeHistoryVO = new ConfigChangeHistoryVO();
            BeanUtil.copyNotNullProperties(configChangeHistoryPO, configChangeHistoryVO);
            configChangeHistoryVOS.add(configChangeHistoryVO);
        }
        return PageInfo.<List<ConfigChangeHistoryVO>>builder().pageNo(pageNo).pageSize(pageSize)
                .totalCount(page.getTotalElements()).data(configChangeHistoryVOS).build();
    }

    /**
     * 指定参数修改历史记录的参数变更列表
     * @param id
     * @param configChangeHistoryId
     * @return
     */
    @Override
    public List<ConfigChangeVO> listConfigChangeByHistory(String id, String configChangeHistoryId) {
        List<ConfigChangeParamPO> configChangeParamPOList =
                configChangeParamRepository.listConfigChangeParamByHistoryId(configChangeHistoryId);
        List<ConfigChangeVO> configChangeParamVOList = new ArrayList<>();
        for (ConfigChangeParamPO configChangeParamPO : configChangeParamPOList) {
            ConfigChangeVO configChangeParamVO = new ConfigChangeVO();
            BeanUtil.copyNotNullProperties(configChangeParamPO, configChangeParamVO);
            configChangeParamVOList.add(configChangeParamVO);
        }
        return configChangeParamVOList;
    }
}

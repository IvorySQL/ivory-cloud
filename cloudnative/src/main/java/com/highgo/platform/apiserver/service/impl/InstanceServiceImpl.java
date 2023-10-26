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
import com.highgo.cloud.constant.OperatorConstant;
import com.highgo.cloud.enums.*;
import com.highgo.cloud.model.PageInfo;
import com.highgo.cloud.util.BeanUtil;
import com.highgo.cloud.util.CommonUtil;
import com.highgo.platform.apiserver.model.dto.ConfigInstanceParamDTO;
import com.highgo.platform.apiserver.model.dto.InstanceDTO;
import com.highgo.platform.apiserver.model.dto.InstanceNetworkDTO;
import com.highgo.platform.apiserver.model.dto.OperationDTO;
import com.highgo.platform.apiserver.model.po.*;
import com.highgo.platform.apiserver.model.vo.request.*;
import com.highgo.platform.apiserver.model.vo.response.ActionResponse;
import com.highgo.platform.apiserver.model.vo.response.InstanceCountVO;
import com.highgo.platform.apiserver.model.vo.response.InstanceEventVO;
import com.highgo.platform.apiserver.model.vo.response.InstanceVO;
import com.highgo.platform.apiserver.repository.*;
import com.highgo.platform.apiserver.service.BackupService;
import com.highgo.platform.apiserver.service.ExtraMetaService;
import com.highgo.platform.apiserver.service.InstanceService;
import com.highgo.platform.apiserver.service.K8sClusterService;
import com.highgo.platform.configuration.K8sClientConfiguration;
import com.highgo.platform.errorcode.BackupError;
import com.highgo.platform.errorcode.ClusterError;
import com.highgo.platform.errorcode.InstanceError;
import com.highgo.platform.exception.BackupException;
import com.highgo.platform.exception.ClusterException;
import com.highgo.platform.exception.InstanceException;
import com.highgo.platform.operator.service.CrService;
import com.highgo.platform.operator.watcher.WatcherFactory;
import com.highgo.platform.websocket.service.WebsocketService;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.storage.StorageClass;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InstanceServiceImpl implements InstanceService {

    private static final Logger logger = LoggerFactory.getLogger(InstanceServiceImpl.class);

    @Autowired
    InstanceRepository instanceRepository;

    @Autowired
    InstanceNetworkRepository instanceNetworkRepository;

    @Autowired
    K8sClusterInfoRepository k8sClusterInfoRepository;

    @Autowired
    BackupPolicyRepository backupPolicyRepository;

    @Autowired
    BackupRepository backupRepository;

    @Autowired
    ConfigDefinationRepository configDefinationRepository;

    @Autowired
    ConfigInstanceParamRepository configInstanceParamRepository;

    @Autowired
    ConfigChangeHistoryRepository configChangeHistoryRepository;

    @Autowired
    InstanceEventRepository instanceEventRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Resource
    WatcherFactory watcherFactory;

    @Resource
    CrService crService;

    @Autowired
    K8sClusterService k8sClusterService;

    @Autowired
    K8sClientConfiguration k8sClientConfiguration;

    @Autowired
    ExtraMetaService extraMetaService;

    @Autowired
    WebsocketService websocketService;

    @Autowired
    BackupService backupService;

    // TODO lcq storageclass
    // @Value("#{${common.storageClassLabels}}")
    // private Map<String, String> storageClassLabels;

    /**
     * 执行创建实例任务
     *
     * @param createInstanceVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InstanceVO createInstance(CreateInstanceVO createInstanceVO) {
        // 权限校验
        InstanceDTO instanceDTO = new InstanceDTO();
        BeanUtil.copyNotNullProperties(createInstanceVO, instanceDTO);
        if (isInstanceExistByClusterAndNamespaceAndName(instanceDTO.getClusterId(), instanceDTO.getNamespace(),
                instanceDTO.getName())) {
            throw new InstanceException(InstanceError.DUPLICATE_NAME);
        }
        // 获取开通实例的集群的配置信息并入库
        k8sClusterService.saveClusterInfo(createInstanceVO.getClusterId());

        // 执行创建实例并返回实例信息
        instanceDTO = createInstance(instanceDTO);
        InstanceVO instanceVO = new InstanceVO();
        BeanUtil.copyNotNullProperties(instanceDTO, instanceVO);
        logger.info(
                "[InstanceServiceImpl.createInstance] create instance success. clusterId:{} namespace:{} name:{} instanceId:{}",
                instanceVO.getClusterId(), instanceVO.getNamespace(), instanceVO.getName(), instanceVO.getId());
        return instanceVO;
    }

    /**
     * 创建实例
     *
     * @param instanceDTO
     * @return
     */
    @Override
    public InstanceDTO createInstance(InstanceDTO instanceDTO) {
        String clusterId = instanceDTO.getClusterId();
        String namespace = instanceDTO.getNamespace();
        String name = instanceDTO.getName();
        // 校验实例是否存在
        if (isInstanceExistByClusterAndNamespaceAndName(clusterId, namespace, name)) {
            // operator onadd事件触发回调方法时，判断实例已存在说明是ui创建的，不再创建实例。
            // 否则认为是k8s创建的cr，反向创建实例
            logger.info(
                    "[InstanceServiceImpl.createInstance] instance is exists already, clusterId {}, namespace {}, name {}, instanceId {}",
                    clusterId, namespace, name, instanceDTO.getId());
            return instanceDTO;
        }

        Date now = CommonUtil.getUTCDate();
        instanceDTO.setStatus(InstanceStatus.CREATING);
        instanceDTO.setCreatedAt(now);
        instanceDTO.setUpdatedAt(now);

        InstancePO instancePO = new InstancePO();
        BeanUtil.copyNotNullProperties(instanceDTO, instancePO);
        instancePO = instanceRepository.save(instancePO);
        String instanceId = instancePO.getId();
        instanceDTO.setId(instanceId);

        // 各产品处理后的扩展属性刷新到instanceDTO
        Map<String, Object> extraMeta = extrametaHandler(instanceDTO);
        instanceDTO.setExtraMeta(extraMeta);

        // 恢复模式
        if (!StringUtils.isEmpty(instanceDTO.getOriginalInstanceId())) {
            validataBeforeRestore(instanceDTO);
        }

        // 初始化实例备份策略
        BackupPolicyPO backupPolicyPO = backupService.initBackupPolicy(instanceId);
        instanceDTO.setBackupPolicyPO(backupPolicyPO);

        // 初始化实例参数
        List<ConfigParamDefinationPO> configParamDefinationPOList = configDefinationRepository.findAll();
        List<ConfigInstanceParamDTO> instanceParamDTOS = new ArrayList<>();
        List<ConfigInstanceParamPO> instanceParamPOS = new ArrayList<>();
        for (ConfigParamDefinationPO configParamDefinationPO : configParamDefinationPOList) {
            ConfigInstanceParamDTO configInstanceParamDTO = new ConfigInstanceParamDTO();
            configInstanceParamDTO.setName(configParamDefinationPO.getName());
            configInstanceParamDTO.setValue(configParamDefinationPO.getDefaultValue());
            configInstanceParamDTO.setInstanceId(instanceId);
            configInstanceParamDTO.setType(configParamDefinationPO.getParamType());
            configInstanceParamDTO.setCreatedAt(now);
            instanceParamDTOS.add(configInstanceParamDTO);
            ConfigInstanceParamPO configInstanceParamPO = new ConfigInstanceParamPO();
            BeanUtil.copyNotNullProperties(configInstanceParamDTO, configInstanceParamPO);
            instanceParamPOS.add(configInstanceParamPO);
        }
        configInstanceParamRepository.saveAll(instanceParamPOS);
        instanceDTO.setConfigInstanceParamDTOS(instanceParamDTOS);

        // 初始化event记录
        InstanceEventPO instanceEventPO = new InstanceEventPO();
        instanceEventPO.setInstanceId(instanceId);
        instanceEventPO.setCreatedAt(now);
        instanceEventRepository.save(instanceEventPO);

        // 实例所在集群上启动watcher
        watcherFactory.startWatcherById(instanceDTO.getClusterId());
        crService.createCr(instanceDTO);
        logger.info("[InstanceServiceImpl.createInstance(InstanceDTO)] create instance success , instanceDTO is {}",
                instanceDTO.toString());
        return instanceDTO;
    }

    /**
     * 创建实例完成回调方法
     */
    @Override
    @Transactional
    public void createInstanceCallback(String instanceId, List<InstanceNetworkDTO> instanceNetworkDTOList,
            String originInstanceId, String originBackupId, boolean result) {
        Optional<InstancePO> instancePOOptional = instanceRepository.findById(instanceId);
        if (!instancePOOptional.isPresent()) {
            logger.error("InstanceServiceImpl.createInstanceCallback. instance is not exist. instanceId is {}",
                    instanceId);
            return;
        }
        InstancePO instancePO = instancePOOptional.get();
        InstanceDTO instanceDTO = new InstanceDTO();
        BeanUtil.copyNotNullProperties(instancePO, instanceDTO);

        // 创建实例失败，不保存网络信息，直接退出
        if (!result) {
            instancePO.setStatus(InstanceStatus.CREATE_FAILED);
            instanceRepository.save(instancePO);
            // 若是恢复模式 更新备份记录状态
            if (!StringUtils.isEmpty(originInstanceId) && !StringUtils.isEmpty(originBackupId)) {
                Optional<BackupPO> backupPOOptional = backupRepository.findById(originBackupId);
                if (backupPOOptional.isPresent()) {
                    BackupPO backupPO = backupPOOptional.get();
                    backupPO.setStatus(BackupStatus.RESTORE_FAILED);
                    backupPO.setUpdatedAt(CommonUtil.getUTCDate());
                    backupPO.setIsRestoring(false);
                    backupRepository.save(backupPO);
                    logger.info(
                            "InstanceServiceImpl.createInstanceCallback. restore mode, backupPO's status is updated. backupid is {} status is {}",
                            originBackupId, backupPO.getStatus());
                }
            }
            // websocketService.sendMsgToUser(instanceDTO,
            // OperationDTO.builder().name(OperationName.CREATE_INSTANCE).status(OperationStatus.FAILED).build(),
            // projectId);
            logger.info(
                    "[InstanceServiceImpl.createInstanceCallback] create instace callback failed. instance id is {}",
                    instanceId);
            return;
        }

        // 创建实例成功
        instancePO.setStatus(InstanceStatus.RUNNING);
        instanceRepository.save(instancePO);
        logger.info(
                "InstanceServiceImpl.createInstanceCallback. instancepo status is updated, instanceid is {}, status is {}",
                instanceId, instancePO.getStatus());
        // 删除旧的网络信息
        instanceNetworkRepository.deleteByInstanceId(instanceId, CommonUtil.getUTCDate());
        K8sClusterInfoPO k8sClusterInfoPO = k8sClusterInfoRepository.findByClusterId(instancePO.getClusterId()).get();
        List<InstanceNetworkPO> instanceNetworkPOList = new ArrayList<>();
        for (InstanceNetworkDTO instanceNetworkDTO : instanceNetworkDTOList) {
            InstanceNetworkPO instanceNetworkPO = new InstanceNetworkPO();
            BeanUtil.copyNotNullProperties(instanceNetworkDTO, instanceNetworkPO);
            instanceNetworkPO.setNodeIp(k8sClusterInfoPO.getServerUrl());
            instanceNetworkPOList.add(instanceNetworkPO);
        }

        instanceNetworkRepository.saveAll(instanceNetworkPOList);
        logger.info("InstanceServiceImpl.createInstanceCallback. network info is saved, instanceid is {}", instanceId);

        // 若是恢复模式 更新备份记录状态
        if (!StringUtils.isEmpty(originInstanceId) && !StringUtils.isEmpty(originBackupId)) {
            Optional<BackupPO> backupPOOptional = backupRepository.findById(originBackupId);
            if (backupPOOptional.isPresent()) {

                BackupPO backupPO = backupPOOptional.get();
                backupPO.setStatus(BackupStatus.RESTORED);
                backupPO.setUpdatedAt(CommonUtil.getUTCDate());
                backupPO.setIsRestoring(false);
                backupRepository.save(backupPO);
                logger.info(
                        "InstanceServiceImpl.createInstanceCallback. restore mode, backupPO's status is updated. backupid is {} status is {}",
                        originBackupId, backupPO.getStatus());
            }
        }
        logger.info("[InstanceServiceImpl.createInstanceCallback] create instace callback success. instance id is {}",
                instanceId);
        // websocketService.sendMsgToUser(instanceDTO,
        // OperationDTO.builder().name(OperationName.CREATE_INSTANCE).status(OperationStatus.SUCCESS).build(),
        // projectId);
    }

    /**
     * 执行删除实例任务
     *
     * @param id
     * @return
     */
    @SneakyThrows
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ActionResponse deleteInstance(String id) {
        InstanceDTO instanceDTO = beforeOperateInstance(id);
        if (!InstanceAllowConstant.ALLOW_DELETE_INSTANCE_STATUS.contains(instanceDTO.getStatus())) {
            logger.error(
                    "[InstanceServiceImpl.deleteInstance]. instance is not allow operate. instanceId is {} status is {}",
                    id, instanceDTO.getStatus());
            throw new InstanceException(InstanceError.INSTANCE_NOT_ALLOW_OPERATE);
        }
        InstancePO instancePO = new InstancePO();
        instanceDTO.setStatus(InstanceStatus.DELETING);
        instanceDTO.setUpdatedAt(CommonUtil.getUTCDate());
        BeanUtil.copyNotNullProperties(instanceDTO, instancePO);
        instanceRepository.save(instancePO);
        crService.deleteCr(instanceDTO);
        logger.info("[InstanceServiceImpl.deleteInstance] delete instance success. instanceId:{}", instanceDTO.getId());
        return ActionResponse.actionSuccess();
    }

    /**
     * 删除成功回调方法
     */
    @Override
    public void deleteInstanceCallback(String id, boolean result) {
        Optional<InstancePO> instancePOOptional = instanceRepository.findById(id);
        if (!instancePOOptional.isPresent()) {
            logger.error("InstanceServiceImpl.deleteInstanceCallback. instance is not exist. instanceId is {}", id);
            return;
        }
        InstancePO instancePO = instancePOOptional.get();
        Date date = CommonUtil.getUTCDate();
        OperationStatus operationStatus;
        if (result) {
            instancePO.setIsDeleted(true);
            instancePO.setDeletedAt(date);
            instancePO.setStatus(InstanceStatus.DELETED);
            operationStatus = OperationStatus.SUCCESS;
        } else {
            instancePO.setStatus(InstanceStatus.DELETE_FAILED);
            operationStatus = OperationStatus.FAILED;
        }
        instanceRepository.save(instancePO);
        // 删除网络信息
        instanceNetworkRepository.deleteByInstanceId(id, date);
        // 删除备份信息
        backupRepository.deleteByInstanceId(id, date);
        // 删除备份策略信息
        backupPolicyRepository.deleteByInstanceId(id, date);
        // 删除配置信息
        configInstanceParamRepository.deleteByInstanceId(id, date);
        configChangeHistoryRepository.deleteByInstanceId(id, date);
        // 删除实例扩展数据
        extraMetaService.deleteByInstanceId(id, date);
        // 删除事件信息
        instanceEventRepository.deleteByInstanceId(id, date);
        logger.info("[InstanceServiceImpl.deleteInstanceCallback] instance {} is deleted!", id);
        InstanceDTO instanceDTO = new InstanceDTO();
        BeanUtil.copyNotNullProperties(instancePO, instanceDTO);
        // String projectId = getProjectIdByNamespace(instanceDTO.getClusterId(), instanceDTO.getNamespace());
        websocketService.sendMsgToUser(instanceDTO,
                OperationDTO.builder().name(OperationName.DELETE_INSTANCE).status(operationStatus).build());
    }

    /**
     * 实例详情
     *
     * @param id
     * @return
     */
    @Override
    public InstanceVO getVO(String id) {
        InstanceDTO instanceDTO = beforeOperateInstance(id);
        InstanceVO instanceVO = new InstanceVO();
        BeanUtil.copyNotNullProperties(instanceDTO, instanceVO);
        List<InstanceNetworkDTO> instanceNetworkDTOs = new ArrayList<>();
        List<InstanceNetworkPO> instanceNetworkPOs = instanceNetworkRepository.listByInstanceId(id);
        for (InstanceNetworkPO instanceNetworkPO : instanceNetworkPOs) {
            InstanceNetworkDTO instanceNetworkDTO = new InstanceNetworkDTO();
            BeanUtil.copyNotNullProperties(instanceNetworkPO, instanceNetworkDTO);
            instanceNetworkDTOs.add(instanceNetworkDTO);
        }
        instanceVO.setNetwork(instanceNetworkDTOs);
        K8sClusterInfoPO k8sClusterInfoPO = k8sClusterService.getInfoByClusterId(instanceDTO.getClusterId());
        instanceVO.setClusterName(k8sClusterInfoPO.getClusterName());
        logger.info("[InstanceServiceImpl.get] get instance info success. instanceId:{}", instanceDTO.getId());
        return instanceVO;
    }

    /**
     * 查询实例DTO
     *
     * @param id
     * @return
     */
    @Override
    public InstanceDTO getDTO(String id) {
        Optional<InstancePO> instancePOOptional = instanceRepository.findById(id);
        if (!instancePOOptional.isPresent()) {
            logger.error("[InstanceServiceImpl.getDTO] instance is not exist. instanceId is {}", id);
            throw new InstanceException(InstanceError.INSTANCE_NOT_EXIST);
        }
        InstancePO instancePO = instancePOOptional.get();
        // 获取各产品的扩展属性，添加到实例详情中
        Map<String, Object> extraMateMap = getSpecialExtraMeta(id);
        InstanceDTO instanceDTO = new InstanceDTO();
        BeanUtil.copyNotNullProperties(instancePO, instanceDTO);
        instanceDTO.setExtraMeta(extraMateMap);
        return instanceDTO;
    }

    /**
     * 实例列表分页
     *
     * @param userId
     * @param filter
     * @param clusterId
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo<List<InstanceVO>> listByFilter(int userId, String filter, String clusterId, int pageNo,
            int pageSize) {
        Map<String, List<InstanceNetworkDTO>> instanceNetworkMap = getInstanceNetworkList();
        // pageNo 界面从第1页开始
        // Pageable 从第0页开始
        // TODO lcq
        // List<String> namespaces = cadService.getNamespaceByProject(projectId, clusterId);
        logger.info("[InstanceServiceImpl.listByFilter] clusterId:{} ", clusterId);
        Specification<InstancePO> specification = new Specification<InstancePO>() {

            @Override
            public Predicate toPredicate(Root<InstancePO> root, CriteriaQuery<?> query,
                    CriteriaBuilder criteriaBuilder) {
                Predicate deletedPredicate = criteriaBuilder.equal(root.get("isDeleted"), false);
                Predicate userPredicate = criteriaBuilder.equal(root.get("creator"), String.valueOf(userId));
                Predicate clusterPredicate = criteriaBuilder.equal(root.get("clusterId"), clusterId);
                // Predicate namespacePredicate = root.get("namespace").in(namespaces);
                Predicate resultPredicate;
                if (!StringUtils.isEmpty(filter)) {
                    Predicate idPredicate = criteriaBuilder.like(root.get("id"), "%" + filter + "%");
                    Predicate namePredicate = criteriaBuilder.like(root.get("name"), "%" + filter + "%");
                    Predicate filterPredicate = criteriaBuilder.or(idPredicate, namePredicate);
                    resultPredicate =
                            criteriaBuilder.and(deletedPredicate, clusterPredicate, userPredicate, filterPredicate);
                } else {
                    resultPredicate = criteriaBuilder.and(deletedPredicate, clusterPredicate, userPredicate);
                }
                query.orderBy(criteriaBuilder.desc(root.get("createdAt")));
                return resultPredicate;
            }
        };

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        List<InstanceVO> instanceVOS = new ArrayList<>();
        Page<InstancePO> page = instanceRepository.findAll(specification, pageable);
        long totalSize = page.getTotalElements();
        for (InstancePO instancePO : page.getContent()) {
            InstanceVO instanceVO = new InstanceVO();
            BeanUtil.copyNotNullProperties(instancePO, instanceVO);
            instanceVO.setNetwork(instanceNetworkMap.get(instanceVO.getId()));
            instanceVOS.add(instanceVO);
        }

        return PageInfo.<List<InstanceVO>>builder().pageNo(pageNo).pageSize(pageSize).totalCount(totalSize)
                .data(instanceVOS).build();

    }

    /**
     * 统计所有项目下实例数量
     *
     * @return
     */
    @Override
    public InstanceCountVO getInstanceCount() {
        int runningNum = 0;
        int createingNum = 0;
        int errorNum = 0;
        String countHql = "SELECT t.status, count(1) from instance t where t.is_deleted = false group by t.status";
        Query countQuery = entityManager.createNativeQuery(countHql);
        List<Object> objects = countQuery.getResultList();
        for (Object obj : objects) {
            Object[] tmp = (Object[]) obj;
            if (InstanceStatus.RUNNING.name().equals((String) tmp[0])) {
                runningNum += ((BigInteger) tmp[1]).intValue();
            } else if (InstanceStatus.CREATING.name().equals((String) tmp[0])) {
                createingNum += ((BigInteger) tmp[1]).intValue();
            } else if (InstanceAllowConstant.ERROR_INSTANCE_STATUS.contains((String) tmp[0])) {
                errorNum += ((BigInteger) tmp[1]).intValue();
            } // 其他状态不做统计

        }
        return InstanceCountVO.builder().errorCount(errorNum).runningCount(runningNum).startingCount(createingNum)
                .build();
    }

    /**
     * 统计所有项目下实例数量
     *
     * @return
     */
    @Override
    public InstanceCountVO getInstanceCountByUser(String userId) {
        int runningNum = 0;
        int createingNum = 0;
        int errorNum = 0;
        String countHql = "";
        Query countQuery = null;
        if ("0".equals(userId)) {
            // admin用户
            countHql = "SELECT t.status, count(1) from instance t where t.is_deleted = false group by t.status";
            countQuery = entityManager.createNativeQuery(countHql);
        } else {
            // 普通用户
            countHql =
                    "SELECT t.status, count(1) from instance t where t.is_deleted = false and t.creator = :userId group by t.status";
            countQuery = entityManager.createNativeQuery(countHql).setParameter("userId", userId);
        }
        List<Object> objects = countQuery.getResultList();
        for (Object obj : objects) {
            Object[] tmp = (Object[]) obj;
            if (InstanceStatus.RUNNING.name().equals((String) tmp[0])) {
                runningNum += ((BigInteger) tmp[1]).intValue();
            } else if (InstanceStatus.CREATING.name().equals((String) tmp[0])) {
                createingNum += ((BigInteger) tmp[1]).intValue();
            } else if (InstanceAllowConstant.ERROR_INSTANCE_STATUS.contains((String) tmp[0])) {
                errorNum += ((BigInteger) tmp[1]).intValue();
            } else {
                // 其他状态不做统计
            }
        }
        return InstanceCountVO.builder().errorCount(errorNum).runningCount(runningNum).startingCount(createingNum)
                .build();
    }

    /**
     * 查询用户下所有实例列表
     *
     * @return
     */
    @Override
    public List<InstanceVO> list(String userId) {
        Map<String, List<InstanceNetworkDTO>> instanceNetworkMap = getInstanceNetworkList();
        List<InstanceVO> instanceVOs = new ArrayList<>();
        for (InstancePO instancePO : instanceRepository.listByUserId(userId)) {
            InstanceVO instanceVO = new InstanceVO();
            BeanUtil.copyNotNullProperties(instancePO, instanceVO);
            Optional<K8sClusterInfoPO> byClusterId =
                    k8sClusterInfoRepository.findByClusterId(instancePO.getClusterId());
            byClusterId.ifPresent(k8sClusterInfoPO -> instanceVO.setClusterName(k8sClusterInfoPO.getClusterName()));
            instanceVO.setNetwork(instanceNetworkMap.get(instanceVO.getId()));
            instanceVOs.add(instanceVO);
        }
        return instanceVOs;
    }

    /**
     * 修改实例描述
     *
     * @param id
     * @param modifyInstanceDescriptionParam
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ActionResponse modifyInstanceDescription(String id,
            ModifyInstanceDescriptionVO modifyInstanceDescriptionParam) {
        InstanceDTO instanceDTO = beforeOperateInstance(id);
        instanceDTO.setDescription(modifyInstanceDescriptionParam.getDescription());
        instanceDTO.setUpdatedAt(CommonUtil.getUTCDate());
        // 入库
        InstancePO instancePO = new InstancePO();
        BeanUtil.copyNotNullProperties(instanceDTO, instancePO);
        instanceRepository.save(instancePO);
        logger.info("[InstanceServiceImpl.modifyInstanceDescription] update instance description success,instanceId:{}",
                instanceDTO.getId());
        return ActionResponse.actionSuccess();
    }

    /**
     * 实例重启
     *
     * @param id
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ActionResponse restartInstance(String id) {
        InstanceDTO instanceDTO = beforeOperateInstance(id);
        if (!InstanceAllowConstant.ALLOW_RESTART_INSTANCE_STATUS.contains(instanceDTO.getStatus())) {
            logger.error(
                    "[InstanceServiceImpl.restartInstance]. instance is not allow operate. instanceId is {} status is {}",
                    id, instanceDTO.getStatus());
            throw new InstanceException(InstanceError.INSTANCE_NOT_ALLOW_OPERATE);
        }
        instanceDTO.setStatus(InstanceStatus.RESTARTING);
        InstancePO instancePO = new InstancePO();
        BeanUtil.copyNotNullProperties(instanceDTO, instancePO);
        instancePO.setUpdatedAt(CommonUtil.getUTCDate());
        instanceRepository.save(instancePO);
        crService.restartDatabase(instanceDTO);
        logger.info("[InstanceServiceImpl.restartInstance] started to restart instance,instanceId:{}",
                instanceDTO.getId());
        return ActionResponse.actionSuccess();
    }

    /**
     * 重启实例回调方法
     *
     * @param id
     */
    @Override
    public void restartInstanceCallback(String id, boolean result) {
        Optional<InstancePO> instancePOOptional = instanceRepository.findById(id);
        if (!instancePOOptional.isPresent()) {
            logger.error("InstanceServiceImpl.restartInstanceCallback. instance is not exist. instanceId id {}", id);
            return;
        }
        InstancePO instancePO = instancePOOptional.get();
        if (!InstanceStatus.RESTARTING.equals(instancePO.getStatus())
                && !InstanceStatus.RESTART_FAILED.equals(instancePO.getStatus())) {
            logger.error(
                    "InstanceServiceImpl.restartInstanceCallback. instance's status is error. instanceId id {} status is {}",
                    id, instancePO.getStatus());
            return;
        }
        OperationStatus operationStatus;
        if (result) {
            instancePO.setStatus(InstanceStatus.RUNNING);
            operationStatus = OperationStatus.SUCCESS;
        } else {
            instancePO.setStatus(InstanceStatus.RESTART_FAILED);
            operationStatus = OperationStatus.FAILED;
        }
        instancePO.setUpdatedAt(CommonUtil.getUTCDate());
        instanceRepository.save(instancePO);
        logger.info(
                "[InstanceServiceImpl.restartInstanceCallback] restart indtance callback, instanceid is {}, restart result is {}",
                id, result);
        InstanceDTO instanceDTO = new InstanceDTO();
        BeanUtil.copyNotNullProperties(instancePO, instanceDTO);
        // String projectId = getProjectIdByNamespace(instanceDTO.getClusterId(), instanceDTO.getNamespace());
        websocketService.sendMsgToUser(instanceDTO,
                OperationDTO.builder().name(OperationName.RESTART_INSTANCE).status(operationStatus).build());
    }

    /**
     * 实例规格变更 cpu memory
     *
     * @param id
     * @param modifyClassParam
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ActionResponse modifyInstance(String id, ModifyClassVO modifyClassParam) {

        InstanceDTO instanceDTO = beforeOperateInstance(id);
        if (!InstanceAllowConstant.ALLOW_MODIFY_INSTANCE_STATUS.contains(instanceDTO.getStatus())) {
            logger.error(
                    "[InstanceServiceImpl.modifyInstance]. instance is not allow operate. instanceId is {} status is {}",
                    id, instanceDTO.getStatus());
            throw new InstanceException(InstanceError.INSTANCE_NOT_ALLOW_OPERATE);
        }
        if (modifyClassParam.getCpu() == null && modifyClassParam.getMemory() == null) {
            return ActionResponse.actionSuccess();
        }
        boolean isCpuEqual = false;
        if (modifyClassParam.getCpu() != null) {
            if (modifyClassParam.getCpu() < instanceDTO.getCpu()) {
                throw new InstanceException(InstanceError.INSTANCE_NOT_ALLOW_DEMOTE);
            }
            if (modifyClassParam.getCpu().equals(instanceDTO.getCpu())) {
                isCpuEqual = true;
            }
            instanceDTO.setCpu(modifyClassParam.getCpu());
        }
        if (modifyClassParam.getMemory() != null) {
            if (modifyClassParam.getMemory() < instanceDTO.getMemory()) {
                throw new InstanceException(InstanceError.INSTANCE_NOT_ALLOW_DEMOTE);
            }
            if (modifyClassParam.getMemory().equals(instanceDTO.getMemory()) && isCpuEqual) {
                return ActionResponse.actionSuccess();
            }
            instanceDTO.setMemory(modifyClassParam.getMemory());
        }
        instanceDTO.setUpdatedAt(CommonUtil.getUTCDate());
        instanceDTO.setStatus(InstanceStatus.UPGRADING);
        InstancePO instancePO = new InstancePO();
        BeanUtil.copyNotNullProperties(instanceDTO, instancePO);
        instanceRepository.save(instancePO);
        crService.patchCrResource(instanceDTO);
        logger.info("[InstanceServiceImpl.modifyInstance]. modify instance, id {}, cpu {}, memory {}", id,
                modifyClassParam.getCpu(), modifyClassParam.getMemory());
        return ActionResponse.actionSuccess();
    }

    /**
     * 规格变更完成回调方法
     *
     * @param id
     */
    @Override
    public void modifyInstanceCallback(String id, boolean result) {
        Optional<InstancePO> instancePOOptional = instanceRepository.findById(id);
        if (!instancePOOptional.isPresent()) {
            logger.error("InstanceServiceImpl.modifyInstanceCallback. instance is not exist. instanceId id {}", id);
            return;
        }
        InstancePO instancePO = instancePOOptional.get();
        if (!InstanceStatus.UPGRADING.equals(instancePO.getStatus())
                && !InstanceStatus.UPGRADE_FLAVOR_FAILED.equals(instancePO.getStatus())) {
            logger.error(
                    "InstanceServiceImpl.modifyInstanceCallback. instance's status is error. instanceId id {} status is {}",
                    id, instancePO.getStatus());
            return;
        }
        OperationStatus operationStatus;
        if (result) {
            instancePO.setStatus(InstanceStatus.RUNNING);
            operationStatus = OperationStatus.SUCCESS;
        } else {
            instancePO.setStatus(InstanceStatus.UPGRADE_FLAVOR_FAILED);
            operationStatus = OperationStatus.FAILED;
        }
        instancePO.setUpdatedAt(CommonUtil.getUTCDate());
        instanceRepository.save(instancePO);
        logger.info("[InstanceServiceImpl.modifyInstanceCallback]. modify instance, id {}, result {}", id, result);
        InstanceDTO instanceDTO = new InstanceDTO();
        BeanUtil.copyNotNullProperties(instancePO, instanceDTO);
        // String projectId = getProjectIdByNamespace(instanceDTO.getClusterId(), instanceDTO.getNamespace());
        websocketService.sendMsgToUser(instanceDTO,
                OperationDTO.builder().name(OperationName.CREATE_BACKUP).status(operationStatus).build());

    }

    /**
     * 磁盘扩容
     *
     * @param id
     * @param modifyStorageParam
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ActionResponse extendInstance(String id, ModifyStorageVO modifyStorageParam) {

        InstanceDTO instanceDTO = beforeOperateInstance(id);
        if (!InstanceAllowConstant.ALLOW_MODIFY_INSTANCE_STATUS.contains(instanceDTO.getStatus())) {
            logger.error(
                    "[InstanceServiceImpl.extendInstance]. instance is not allow operate. instanceId is {} status is {}",
                    id, instanceDTO.getStatus());
            throw new InstanceException(InstanceError.INSTANCE_NOT_ALLOW_OPERATE);
        }
        if (modifyStorageParam.getStorageSize() <= instanceDTO.getStorage()) {
            throw new InstanceException(InstanceError.INSTANCE_NOT_ALLOW_SHRINKAGE);
        }
        instanceDTO.setStorage(modifyStorageParam.getStorageSize());
        instanceDTO.setUpdatedAt(CommonUtil.getUTCDate());
        instanceDTO.setStatus(InstanceStatus.EXTENDING);
        InstancePO instancePO = new InstancePO();
        BeanUtil.copyNotNullProperties(instanceDTO, instancePO);
        instancePO.setUpdatedAt(CommonUtil.getUTCDate());
        instanceRepository.save(instancePO);
        crService.patchCrStorage(instanceDTO);
        logger.info("[InstanceServiceImpl.extendInstance]. instance id {}, storage {}", id,
                modifyStorageParam.getStorageSize());
        return ActionResponse.actionSuccess();
    }

    /**
     * 磁盘扩容完成回调方法
     *
     * @param id
     * @param result
     */
    @Override
    public void extendInstanceCallback(String id, boolean result) {
        Optional<InstancePO> instancePOOptional = instanceRepository.findById(id);
        if (!instancePOOptional.isPresent()) {
            logger.error("InstanceServiceImpl.extendInstanceCallback. instance is not exist. instanceId id {}", id);
            return;
        }
        InstancePO instancePO = instancePOOptional.get();
        if (!InstanceStatus.EXTENDING.equals(instancePO.getStatus())) {
            logger.error(
                    "InstanceServiceImpl.extendInstanceCallback. instance's status is not extending. instanceId id {} status is {}",
                    id, instancePO.getStatus());
            return;
        }
        OperationStatus operationStatus;
        if (result) {
            instancePO.setStatus(InstanceStatus.RUNNING);
            operationStatus = OperationStatus.SUCCESS;
        } else {
            instancePO.setStatus(InstanceStatus.EXTEND_STORAGE_FAILED);
            operationStatus = OperationStatus.FAILED;
        }
        instancePO.setUpdatedAt(CommonUtil.getUTCDate());
        instanceRepository.save(instancePO);
        logger.info("[InstanceServiceImpl.extendInstanceCallback]. instance id {}, result {}", id, result);
        InstanceDTO instanceDTO = new InstanceDTO();
        BeanUtil.copyNotNullProperties(instancePO, instanceDTO);
        // String projectId = getProjectIdByNamespace(instanceDTO.getClusterId(), instanceDTO.getNamespace());
        websocketService.sendMsgToUser(instanceDTO,
                OperationDTO.builder().name(OperationName.EXTEND_STORAGE).status(operationStatus).build());

    }

    /**
     * 执行开启或关闭外网任务
     *
     * @param id
     * @param modifySwitchVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ActionResponse modifyNodeportSwitch(String id, ModifySwitchVO modifySwitchVO) {
        InstanceDTO instanceDTO = beforeOperateInstance(id);
        if (!InstanceAllowConstant.ALLOW_MODIFY_INSTANCE_STATUS.contains(instanceDTO.getStatus())) {
            logger.error(
                    "[InstanceServiceImpl.modifyNodeportSwitch]. instance is not allow operate. instanceId is {} status is {}",
                    id, instanceDTO.getStatus());
            throw new InstanceException(InstanceError.INSTANCE_NOT_ALLOW_OPERATE);
        }
        if (modifySwitchVO.getSwitchStatus().equals(instanceDTO.getNodePortSwitch())) {
            throw new InstanceException(InstanceError.INSTANCE_NO_CHANGE);
        }
        instanceDTO.setNodePortSwitch(modifySwitchVO.getSwitchStatus());
        instanceDTO.setUpdatedAt(CommonUtil.getUTCDate());
        if (SwitchStatus.ON.equals(modifySwitchVO.getSwitchStatus())) {
            instanceDTO.setStatus(InstanceStatus.NODEPORT_OPENING);
        } else {
            instanceDTO.setStatus(InstanceStatus.NODEPORT_CLOSING);
        }
        InstancePO instancePO = new InstancePO();
        BeanUtil.copyNotNullProperties(instanceDTO, instancePO);
        instancePO.setUpdatedAt(CommonUtil.getUTCDate());
        instanceRepository.save(instancePO);
        crService.nodeportSwitch(instanceDTO);
        logger.info("[InstanceServiceImpl.modifyNodeportSwitch]. instance id {}, switch {}", id,
                modifySwitchVO.getSwitchStatus());
        return ActionResponse.actionSuccess();
    }

    /**
     * 开启/关闭外网完成回调方法
     */
    @Override
    public void openNodeportSwitchCallback(String id, Integer nodeportRW, Integer nodeportRO, boolean result) {
        // 开启外网
        // 保存port到数据库
        Optional<InstancePO> instancePOOptional = instanceRepository.findById(id);
        if (!instancePOOptional.isPresent()) {
            logger.error("InstanceServiceImpl.openNodeportSwitchCallback. instance is not exist. instanceId id {}", id);
            return;
        }
        InstancePO instancePO = instancePOOptional.get();
        OperationStatus operationStatus;
        if (result) {
            Optional<K8sClusterInfoPO> k8sClusterInfoPOOptional =
                    k8sClusterInfoRepository.findByClusterId(instancePO.getClusterId());
            K8sClusterInfoPO k8sClusterInfoPO = k8sClusterInfoPOOptional.get();
            String k8sIP = k8sClusterInfoPO.getServerUrl();
            instancePO.setStatus(InstanceStatus.RUNNING);
            instancePO.setNodePortSwitch(SwitchStatus.ON);
            Optional<InstanceNetworkPO> instanceNetworkPORW =
                    instanceNetworkRepository.findByIdAndType(id, NetworkType.RW);
            if (instanceNetworkPORW.isPresent()) {
                InstanceNetworkPO instanceNetworkPO = instanceNetworkPORW.get();
                instanceNetworkPO.setNodeIp(k8sIP);
                instanceNetworkPO.setNodePort(nodeportRW);
                instanceNetworkRepository.save(instanceNetworkPO);
            }
            Optional<InstanceNetworkPO> instanceNetworkPORO =
                    instanceNetworkRepository.findByIdAndType(id, NetworkType.RO);
            if (instanceNetworkPORO.isPresent()) {
                InstanceNetworkPO instanceNetworkPO = instanceNetworkPORO.get();
                instanceNetworkPO.setNodeIp(k8sIP);
                instanceNetworkPO.setNodePort(nodeportRO);
                instanceNetworkRepository.save(instanceNetworkPO);
            }
            operationStatus = OperationStatus.SUCCESS;
        } else {
            instancePO.setStatus(InstanceStatus.NODEPORT_OPEN_FAILED);
            operationStatus = OperationStatus.SUCCESS;
        }
        instancePO.setUpdatedAt(CommonUtil.getUTCDate());
        instancePO.setUpdatedAt(CommonUtil.getUTCDate());
        instanceRepository.save(instancePO);
        logger.info("[InstanceServiceImpl.openNodeportSwitchCallback]. instance id {}, result {}", id, result);
        InstanceDTO instanceDTO = new InstanceDTO();
        BeanUtil.copyNotNullProperties(instancePO, instanceDTO);
        // String projectId = getProjectIdByNamespace(instanceDTO.getClusterId(), instanceDTO.getNamespace());
        websocketService.sendMsgToUser(instanceDTO,
                OperationDTO.builder().name(OperationName.OPEN_NODEPORT).status(operationStatus).build());
    }

    /**
     * 关闭外网完成回调方法
     */
    @Override
    public void closeNodeportSwitchCallback(String id, boolean result) {
        // 关闭外网
        // 从数据库删除port
        Optional<InstancePO> instancePOOptional = instanceRepository.findById(id);
        if (!instancePOOptional.isPresent()) {
            logger.error("InstanceServiceImpl.closeNodeportSwitchCallback. instance is not exist. instanceId id {}",
                    id);
            return;
        }
        InstancePO instancePO = instancePOOptional.get();
        OperationStatus operationStatus;
        if (result) {
            instancePO.setStatus(InstanceStatus.RUNNING);
            instancePO.setNodePortSwitch(SwitchStatus.OFF);
            Optional<InstanceNetworkPO> instanceNetworkPORWOptional =
                    instanceNetworkRepository.findByIdAndType(id, NetworkType.RW);
            if (instanceNetworkPORWOptional.isPresent()) {
                InstanceNetworkPO instanceNetworkPORW = instanceNetworkPORWOptional.get();
                instanceNetworkPORW.setNodePort(null);
                instanceNetworkPORW.setNodeIp(null);
                instanceNetworkRepository.save(instanceNetworkPORW);
            }
            Optional<InstanceNetworkPO> instanceNetworkPOROOptional =
                    instanceNetworkRepository.findByIdAndType(id, NetworkType.RO);
            if (instanceNetworkPOROOptional.isPresent()) {
                InstanceNetworkPO instanceNetworkPORO = instanceNetworkPOROOptional.get();
                instanceNetworkPORO.setNodePort(null);
                instanceNetworkPORO.setNodeIp(null);
                instanceNetworkRepository.save(instanceNetworkPORO);
            }
            operationStatus = OperationStatus.SUCCESS;
        } else {
            instancePO.setStatus(InstanceStatus.NODEPORT_CLOSE_FAILED);
            operationStatus = OperationStatus.FAILED;
        }
        instancePO.setUpdatedAt(CommonUtil.getUTCDate());
        instanceRepository.save(instancePO);
        logger.info("[InstanceServiceImpl.closeNodeportSwitchCallback]. instance id {}, result {}", id, result);
        InstanceDTO instanceDTO = new InstanceDTO();
        BeanUtil.copyNotNullProperties(instancePO, instanceDTO);
        // String projectId = getProjectIdByNamespace(instanceDTO.getClusterId(), instanceDTO.getNamespace());
        websocketService.sendMsgToUser(instanceDTO,
                OperationDTO.builder().name(OperationName.CLOSE_NODEPORT).status(operationStatus).build());
    }

    /**
     * 操作实例前处理工作
     * 1 校验实例是否存在
     * 2 校验实例是否有权限
     * 3 返回一个实例内部传输对象DTO
     *
     * @param instanceId
     * @return
     */
    @Override
    public InstanceDTO beforeOperateInstance(String instanceId) {
        InstanceDTO instanceDTO = getDTO(instanceId);
        // checkInstanceAuth(instanceDTO.getClusterId(), projectId, instanceDTO.getNamespace());
        return instanceDTO;
    }

    /**
     * 校验实例名称是否唯一(同一集群同一命名空间内)
     *
     * @param verifyInstanceNameVO
     * @return
     */
    @Override
    public ActionResponse instanceNameUniqueCheck(VerifyInstanceNameVO verifyInstanceNameVO) {
        InstanceDTO instanceDTO = new InstanceDTO();
        instanceDTO.setClusterId(verifyInstanceNameVO.getClusterId());
        instanceDTO.setNamespace(verifyInstanceNameVO.getNamespace());
        instanceDTO.setName(verifyInstanceNameVO.getName());
        if (isInstanceExistByClusterAndNamespaceAndName(instanceDTO.getClusterId(), instanceDTO.getNamespace(),
                instanceDTO.getName())) {
            return ActionResponse.actionFailed();
        } else {
            return ActionResponse.actionSuccess();
        }
    }

    /**
     * 获取实例事件信息
     *
     * @param instanceId 实例id
     * @return
     */
    @Override
    public InstanceEventVO getEvent(String instanceId) {
        InstanceDTO instanceDTO = beforeOperateInstance(instanceId);
        InstanceEventVO instanceEventVO = new InstanceEventVO();
        Optional<InstanceEventPO> instanceEventPOOptional = instanceEventRepository.findByInstanceId(instanceId);
        if (!instanceEventPOOptional.isPresent()) {
            instanceEventVO.setInstanceId(instanceDTO.getId());
            return instanceEventVO;
        }
        InstanceEventPO instanceEventPO = instanceEventPOOptional.get();
        BeanUtil.copyNotNullProperties(instanceEventPO, instanceEventVO);
        return instanceEventVO;
    }

    /**
     * 更新resource version
     *
     * @param instanceId      实例id
     * @param resourceVersion
     */
    @Override
    public void updateResourseVersion(String instanceId, long resourceVersion) {
        instanceEventRepository.updateResourceVersionByInstanceId(instanceId, resourceVersion, CommonUtil.getUTCDate());
    }

    /**
     * 获取resource version
     *
     * @param instanceId
     * @return
     */
    @Override
    public Long getResourceVersion(String instanceId) {
        return instanceEventRepository.getResourceVersion(instanceId);
    }

    /**
     * 更新实例副本数量
     *
     * @param instanceId
     * @param nodeNum
     */
    @Override
    public void updateNodeNum(String instanceId, int nodeNum) {
        instanceEventRepository.updateNodeNumByInstanceId(instanceId, nodeNum, CommonUtil.getUTCDate());
    }

    /**
     * 更新实例ststefulset事件信息
     *
     * @param instanceId
     * @param stsEvent
     */
    @Override
    public void updateStsEvent(String instanceId, String stsEvent) {
        instanceEventRepository.updateStsEventByInstanceId(instanceId, stsEvent, CommonUtil.getUTCDate());
    }

    /**
     * 更新实例pod 事件信息
     *
     * @param instanceId
     * @param podEvent
     */
    @Override
    public void updatePodEvent(String instanceId, String podEvent) {
        instanceEventRepository.updatePodEventByInstanceId(instanceId, podEvent, CommonUtil.getUTCDate());
    }

    /**
     * 更新实例 ready节点数量
     *
     * @param instanceId
     * @param nodeReadyNum
     */
    @Override
    public void updateNodeReadyNum(String instanceId, int nodeReadyNum) {
        instanceEventRepository.updateNodeReadyNumByInstanceId(instanceId, nodeReadyNum, CommonUtil.getUTCDate());
    }

    @Override
    public void updateNodeEvent(String instanceId, int readyNum, String stsEvent, String podEvent) {
        instanceEventRepository.updateEventByInstanceId(instanceId, readyNum, stsEvent, podEvent,
                CommonUtil.getUTCDate());
    }

    /**
     * 获取实例状态
     *
     * @param instanceId
     * @return
     */
    @Override
    public InstanceStatus getInstanceStatus(String instanceId) {
        Optional<InstancePO> instancePOOptional = instanceRepository.findById(instanceId);
        if (!instancePOOptional.isPresent()) {
            return null;
        }
        InstancePO instancePO = instancePOOptional.get();
        return instancePO.getStatus();
    }

    /**
     * 更新实例状态
     *
     * @param instanceId     实例id
     * @param instanceStatus 实例状态
     */
    @Override
    public void updateInstanceStatus(String instanceId, InstanceStatus instanceStatus) {
        instanceRepository.updateStatusByInstanceId(instanceId, CommonUtil.getUTCDate(), instanceStatus);
    }

    /**
     * 获取集群的存储信息
     *
     * @param clusterId
     * @return
     */
    @Override
    public List<StorageClass> getStorageClasses(String clusterId) {
        KubernetesClient kubernetesClient = k8sClientConfiguration.getAdminKubernetesClientById(clusterId);
        return kubernetesClient.storage().storageClasses().list().getItems();
    }

    /**
     * 获取实例master 节点pod信息
     *
     * @return
     */
    @Override
    public Pod getMasterPod(String instanceId) {
        InstanceDTO instanceDTO = beforeOperateInstance(instanceId);
        return crService.getMasterPod(instanceDTO);
    }

    @Override
    public String getProjectIdByNamespace(String clusterId, String namespaceStr) {
        KubernetesClient kubernetesClient = k8sClientConfiguration.getAdminKubernetesClientById(clusterId);
        Namespace namespace = kubernetesClient.namespaces().withName(namespaceStr).get();
        if (namespace == null) {
            logger.error(
                    "[InstanceServiceImpl.getProjectIdByNamespace] namespace is not exist. clusterId {} namespace {}",
                    clusterId, namespaceStr);
            return null;
        }
        String projectId = namespace.getMetadata().getLabels().get("project_id");
        if (projectId == null) {
            logger.error(
                    "[InstanceServiceImpl.getProjectIdByNamespace] namespace no label project_id, clusterId {} namespace {}",
                    clusterId, namespaceStr);
        }
        return projectId;
    }

    /**
     * 开放接口， 获取数据库产品特殊扩展属性集合（Map）
     *
     * @return Map<String, Object>
     */
    @Override
    public Map<String, Object> getSpecialExtraMeta(String indtanceId) {

        List<ExtraMetaPO> extraMetaPOS = extraMetaService.findAllByInstanceId(indtanceId);

        return extraMetaPOS
                .stream()
                .collect(Collectors.toMap(ExtraMetaPO::getName, ExtraMetaPO::getValue));

    }

    @Override
    public ActionResponse getHgadminUrl(String instanceId) {

        InstanceDTO dto = getDTO(instanceId);

        String pgadminPort = String.valueOf(dto.getExtraMeta().get(OperatorConstant.PGADMINPORT));
        if (StringUtils.isEmpty(pgadminPort) || "null".equals(pgadminPort)) {
            Integer port = createInstanceHgadminCallback(instanceId);
            if (port != null) {
                pgadminPort = String.valueOf(port);
            } else {
                return ActionResponse.actionFailed("Service exception, please try again.");
            }
        }
        Optional<K8sClusterInfoPO> byClusterId = k8sClusterInfoRepository.findByClusterId(dto.getClusterId());
        if (!byClusterId.isPresent()) {
            logger.error("[MonitorServiceImpl.createMonitor] cluster is not exits. clusterId is {}",
                    dto.getClusterId());
            throw new ClusterException(ClusterError.CLUSTER_NOT_EXIST_ERROR);
        }
        String serverUrl = byClusterId.get().getServerUrl();
        return ActionResponse.actionSuccess(serverUrl + ":" + pgadminPort);
    }

    @Override
    public Integer createInstanceHgadminCallback(String instanceId) {
        Optional<ExtraMetaPO> extraMetaByInstanceIdAndName =
                extraMetaService.findExtraMetaByInstanceIdAndName(instanceId, OperatorConstant.PGADMINPORT);
        Integer port = crService.getHgadminPort(instanceId);
        if (extraMetaByInstanceIdAndName.isPresent()) {
            extraMetaService.updateValueByNameAndInstanceId(instanceId, OperatorConstant.PGADMINPORT,
                    String.valueOf(port));
        } else {
            extraMetaService.saveExtraMeta(instanceId, OperatorConstant.PGADMINPORT, String.valueOf(port));
        }
        return port;
    }

    @Override
    public void updateInstanceCpuResource(String instanceId, Integer cpuResource) {
        instanceRepository.updateCpuByInstanceId(instanceId, CommonUtil.getUTCDate(), cpuResource);
    }

    @Override
    public void updateInstanceMemoryResource(String instanceId, Integer memoryResource) {
        instanceRepository.updateMemoryByInstanceId(instanceId, CommonUtil.getUTCDate(), memoryResource);
    }

    /**
     * 开放接口， 各产品处理各自的扩展数据，校验、入库、添加默认扩展数据等
     *
     * @param instanceDTO
     * @return
     */
    @Override
    public Map<String, Object> extrametaHandler(InstanceDTO instanceDTO) {
        return instanceDTO.getExtraMeta();
    }

    /**
     * 恢复实例前校验
     *
     * @param instanceDTO
     */
    public void validataBeforeRestore(InstanceDTO instanceDTO) {
        // 从其他实例的备份恢复
        Optional<InstancePO> optionalInstancePOOriginal =
                instanceRepository.findById(instanceDTO.getOriginalInstanceId());
        // 实例不存在或已删除
        if (!optionalInstancePOOriginal.isPresent() || optionalInstancePOOriginal.get().getIsDeleted()) {
            logger.error(
                    "[InstanceServiceImpl.validataBeforeRestore] restore instance, origin instance is not exist, instance id is {}",
                    instanceDTO.getOriginalInstanceId());
            throw new InstanceException(InstanceError.INSTANCE_NOT_EXIST);
        }
        InstancePO instancePOOrigin = optionalInstancePOOriginal.get();
        // 实例当前状态不允许执行恢复
        if (!InstanceAllowConstant.ALLOW_RESTORE_INSTANCE_STATUS.contains(instancePOOrigin.getStatus())) {
            throw new InstanceException(InstanceError.INSTANCE_NOT_ALLOW_OPERATE);
        }
        Optional<BackupPO> optionalBackupPO = backupRepository.findById(instanceDTO.getOriginalBackupId());
        // 备份不存在
        if (!optionalBackupPO.isPresent()) {
            throw new BackupException(BackupError.BACKUP_NOT_EXIST);
        }
        BackupPO backupPO = optionalBackupPO.get();
        // 备份当前状态不允许执行恢复
        if (!InstanceAllowConstant.ALLOW_RESTORE_BACKUP_STATUS.contains(backupPO.getStatus())) {
            throw new BackupException(BackupError.BACKUP_NOT_ALLOW_OPERATE);
        }
        if (instancePOOrigin.getCpu() > instanceDTO.getCpu() || instancePOOrigin.getMemory() > instanceDTO.getMemory()
                ||
                instancePOOrigin.getStorage() > instanceDTO.getStorage()) {
            throw new InstanceException(InstanceError.INSTANCE_NOT_ALLOW_DEMOTE);
        }
        // 设置备份记录的状态为恢复中
        backupPO.setStatus(BackupStatus.RESTORING);
        backupPO.setIsRestoring(true);
        backupPO.setLastRecoveryTime(CommonUtil.getUTCDate());
        backupRepository.save(backupPO);

    }

    /**
     * 检查同名实例是否存在
     *
     * @return 存在-true 不存在-false
     */
    public boolean isInstanceExistByClusterAndNamespaceAndName(String clusterId, String namespace,
            String instanceName) {
        if (instanceRepository.countByClusterAndNamespaceAndName(clusterId, namespace, instanceName) > 0) {
            return true;
        } else {
            return false;
        }
    }

    private Map<String, List<InstanceNetworkDTO>> getInstanceNetworkList() {
        Map<String, List<InstanceNetworkDTO>> instanceNetworkMap = new HashMap<>();
        List<InstanceNetworkPO> instanceNetworkPOList = instanceNetworkRepository.list();
        for (InstanceNetworkPO instanceNetworkPO : instanceNetworkPOList) {
            if (!instanceNetworkMap.containsKey(instanceNetworkPO.getInstanceId())) {
                instanceNetworkMap.put(instanceNetworkPO.getInstanceId(), new ArrayList<>());
            }
            InstanceNetworkDTO instanceNetworkDTO = new InstanceNetworkDTO();
            BeanUtil.copyNotNullProperties(instanceNetworkPO, instanceNetworkDTO);
            instanceNetworkMap.get(instanceNetworkPO.getInstanceId()).add(instanceNetworkDTO);
        }
        return instanceNetworkMap;
    }

}

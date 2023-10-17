package com.highgo.platform.apiserver.service.impl;

import com.highgo.cloud.constant.InstanceAllowConstant;
import com.highgo.cloud.enums.*;
import com.highgo.cloud.model.PageInfo;
import com.highgo.cloud.util.BeanUtil;
import com.highgo.cloud.util.CommonUtil;
import com.highgo.platform.apiserver.model.dto.BackupDTO;
import com.highgo.platform.apiserver.model.dto.BackupPolicyDTO;
import com.highgo.platform.apiserver.model.dto.InstanceDTO;
import com.highgo.platform.apiserver.model.dto.OperationDTO;
import com.highgo.platform.apiserver.model.po.BackupPO;
import com.highgo.platform.apiserver.model.po.BackupPolicyPO;
import com.highgo.platform.apiserver.model.vo.request.CreateBackupVO;
import com.highgo.platform.apiserver.model.vo.request.ModifyAutoBackupSwitchVO;
import com.highgo.platform.apiserver.model.vo.request.ModifyBackupPolicyVO;
import com.highgo.platform.apiserver.model.vo.response.ActionResponse;
import com.highgo.platform.apiserver.model.vo.response.BackupPolicyVO;
import com.highgo.platform.apiserver.model.vo.response.BackupVO;
import com.highgo.platform.apiserver.repository.BackupPolicyRepository;
import com.highgo.platform.apiserver.repository.BackupRepository;
import com.highgo.platform.apiserver.service.BackupService;
import com.highgo.platform.apiserver.service.InstanceService;
import com.highgo.platform.errorcode.BackupError;
import com.highgo.platform.errorcode.InstanceError;
import com.highgo.platform.exception.BackupException;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class BackupServiceImpl implements BackupService {

    private static final Logger logger = LoggerFactory.getLogger(BackupServiceImpl.class);

    @Autowired
    private InstanceService instanceService;

    @Autowired
    private CrService crService;

    @Autowired
    private BackupRepository backupRepository;

    @Autowired
    private BackupPolicyRepository backupPolicyRepository;

    @Autowired
    private WebsocketService websocketService;

    /**
     * 创建备份
     *
     * @param createBackupParam
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BackupVO createBackup(String instanceId, CreateBackupVO createBackupParam) {
        //1 校验是否有权限
        InstanceDTO instanceDTO = instanceService.beforeOperateInstance(instanceId);
        if (!InstanceAllowConstant.ALLOW_BACKUP_INSTANCE_STATUS.contains(instanceDTO.getStatus())) {
            throw new InstanceException(InstanceError.INSTANCE_NOT_ALLOW_OPERATE);
        }
        //2 备份入库
        BackupDTO backupDTO = createBackupRecord(instanceId, createBackupParam);
        //3 operator创建备份
        BackupVO backupVO = new BackupVO();
        BeanUtil.copyNotNullProperties(backupDTO, backupVO);
        crService.createBackup(backupDTO);
        logger.info("[BackupServiceImpl.createBackup] create backup manual job success. instanceId is {} backupId is {}", instanceId, backupDTO.getId());
        return backupVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BackupDTO createBackupRecord(String instanceId, CreateBackupVO createBackupParam) {
        BackupDTO backupDTO = new BackupDTO();
        BeanUtil.copyNotNullProperties(createBackupParam, backupDTO);
        backupDTO.setInstanceId(instanceId);
        backupDTO.setStatus(BackupStatus.PROCESSING);
        backupDTO.setBackupMethod(createBackupParam.getBackupMethod());
        backupDTO.setCreatedAt(CommonUtil.getUTCDate());
        BackupPO backupPO = new BackupPO();
        BeanUtil.copyNotNullProperties(backupDTO, backupPO);
        backupPO = backupRepository.save(backupPO);
        backupDTO.setId(backupPO.getId());
        InstanceDTO instanceDTO = instanceService.getDTO(instanceId);
        if (InstanceStatus.RUNNING.equals(instanceDTO.getStatus())) {
            // 触发备份逻辑时且实例是正常运行中时，同步修改实例状态为备份中。(实例为中间状态时保留原始状态)
            instanceService.updateInstanceStatus(instanceId, InstanceStatus.BACKUPING);
        }
        logger.info("[BackupServiceImpl.createBackup] create backup job success. instanceId is {} backupId is {} method is {}", instanceId, backupPO.getId(), createBackupParam.getBackupMethod());
        return backupDTO;
    }

    /**
     * 创建备份完成回调方法
     *
     * @param filename 备份文件名称
     * @param backupId 备份id
     * @param result   备份结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createBackupCallback(String backupId, String filename, boolean result) {
        Optional<BackupPO> backupPOOptional = backupRepository.findById(backupId);
        BackupPO backupPO = backupPOOptional.get();
        backupPO.setUpdatedAt(CommonUtil.getUTCDate());
        backupPO.setFileName(filename);
        OperationStatus operationStatus;
        if (result) {
            backupPO.setStatus(BackupStatus.COMPLETED);
            operationStatus = OperationStatus.SUCCESS;
        } else {
            backupPO.setStatus(BackupStatus.FAILED);
            operationStatus = OperationStatus.FAILED;
        }
        backupRepository.save(backupPO);
        logger.info("[BackupServiceImpl.createBackupCallback] create backup manual callback success. instanceId is {} backupId is {} filename is {}", backupPO.getInstanceId(), backupPO.getId(), filename);
        InstanceDTO instanceDTO = instanceService.getDTO(backupPO.getInstanceId());
        if (InstanceStatus.BACKUPING.equals(instanceDTO.getStatus())) {
            instanceService.updateInstanceStatus(instanceDTO.getId(), InstanceStatus.RUNNING);
        }
        //String projectId = instanceService.getProjectIdByNamespace(instanceDTO.getClusterId(), instanceDTO.getNamespace());
        websocketService.sendMsgToUser(instanceDTO, OperationDTO.builder().name(OperationName.CREATE_BACKUP).status(operationStatus).build());
    }

    /**
     * 创建备份完成回调方法
     *
     * @param backupId 备份id
     * @param result   备份结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createBackupCallback(String backupId, boolean result) {
        Optional<BackupPO> backupPOOptional = backupRepository.findById(backupId);
        BackupPO backupPO = backupPOOptional.get();
        backupPO.setUpdatedAt(CommonUtil.getUTCDate());
        OperationStatus operationStatus;
        if (result) {
            backupPO.setStatus(BackupStatus.COMPLETED);
            operationStatus = OperationStatus.SUCCESS;
        } else {
            backupPO.setStatus(BackupStatus.FAILED);
            operationStatus = OperationStatus.FAILED;
        }
        backupRepository.save(backupPO);
        logger.info("[BackupServiceImpl.createBackupCallback] create backup manual callback success. instanceId is {} backupId is {}", backupPO.getInstanceId(), backupPO.getId());
        InstanceDTO instanceDTO = instanceService.getDTO(backupPO.getInstanceId());
        if (InstanceStatus.BACKUPING.equals(instanceDTO.getStatus())) {
            instanceService.updateInstanceStatus(instanceDTO.getId(), InstanceStatus.RUNNING);
        }
        //String projectId = instanceService.getProjectIdByNamespace(instanceDTO.getClusterId(), instanceDTO.getNamespace());
        websocketService.sendMsgToUser(instanceDTO, OperationDTO.builder().name(OperationName.CREATE_BACKUP).status(operationStatus).build());
    }


    /**
     * 查询备份策略
     *
     * @param id
     * @return
     */
    @Override
    public BackupPolicyVO getBackupPolicy(String id) {
        Optional<BackupPolicyPO> backupPolicyPOOptional = backupPolicyRepository.findByInstanceId(id);
        BackupPolicyVO backupPolicyVO = new BackupPolicyVO();
        BackupPolicyPO backupPolicyPO = backupPolicyPOOptional.get();
        BeanUtil.copyNotNullProperties(backupPolicyPO, backupPolicyVO);
        return backupPolicyVO;
    }

    /**
     * 修改备份策略
     *
     * @param id
     * @param modifyBackupPolicyParam
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BackupPolicyVO modifyBackupPolicy(String id, ModifyBackupPolicyVO modifyBackupPolicyParam) {
        InstanceDTO instanceDTO = instanceService.beforeOperateInstance(id);
        Optional<BackupPolicyPO> backupPolicyPOOptional = backupPolicyRepository.findByInstanceId(instanceDTO.getId());
        BackupPolicyPO backupPolicyPO = backupPolicyPOOptional.get();
        // 没有变更直接return
        if (StringUtils.isEmpty(modifyBackupPolicyParam.getBackupPeriod()) && modifyBackupPolicyParam.getStartTime() == null &&
                modifyBackupPolicyParam.getBackupMode() == null && modifyBackupPolicyParam.getBackupType() == null) {
            BackupPolicyVO backupPolicyVO = new BackupPolicyVO();
            BeanUtil.copyNotNullProperties(backupPolicyPO, backupPolicyVO);
        }
        // 修改参数赋值
        BeanUtil.copyNotNullProperties(modifyBackupPolicyParam, backupPolicyPO);
        backupPolicyPO.setUpdatedAt(CommonUtil.getUTCDate());
        if (SwitchStatus.ON.equals(backupPolicyPO.getStatus())) {
            // 自动备份是开启状态 下发任务到cr
            BackupPolicyDTO backupPolicyDTO = new BackupPolicyDTO();
            BeanUtil.copyNotNullProperties(backupPolicyPO, backupPolicyDTO);
            crService.applyBackupPolicy(backupPolicyDTO);
        }
        backupPolicyRepository.save(backupPolicyPO);
        BackupPolicyVO backupPolicyVO = new BackupPolicyVO();
        BeanUtil.copyNotNullProperties(backupPolicyPO, backupPolicyVO);
        logger.info("[BackupServiceImpl.modifyBackupPolicy] modify backup policy success. instanceId is {} backupPolicyVO is {} ", id, backupPolicyVO.toString());
        return backupPolicyVO;
    }

    /**
     * 开启/关闭自动备份
     *
     * @param id
     * @param modifyAutoBackupSwitchParam
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ActionResponse modifyBackupSwitch(String id, ModifyAutoBackupSwitchVO modifyAutoBackupSwitchParam) {
        InstanceDTO instanceDTO = instanceService.beforeOperateInstance(id);
        Optional<BackupPolicyPO> backupPolicyPOOptional = backupPolicyRepository.findByInstanceId(instanceDTO.getId());
        BackupPolicyPO backupPolicyPO = backupPolicyPOOptional.get();
        if (backupPolicyPO.getStatus().equals(modifyAutoBackupSwitchParam.getSwitchStatus())) {
            throw new InstanceException(InstanceError.INSTANCE_NO_CHANGE);
        }
        backupPolicyPO.setStatus(modifyAutoBackupSwitchParam.getSwitchStatus());
        backupPolicyPO.setUpdatedAt(CommonUtil.getUTCDate());
        backupPolicyRepository.save(backupPolicyPO);
        BackupPolicyDTO backupPolicyDTO = new BackupPolicyDTO();
        BeanUtil.copyNotNullProperties(backupPolicyPO, backupPolicyDTO);
        crService.applyBackupPolicy(backupPolicyDTO);
        logger.info("[BackupServiceImpl.modifyBackupSwitch] modify auto backup switch. instanceId is {} switch status is {}", id, backupPolicyDTO.getStatus().name());
        return ActionResponse.actionSuccess();
    }

    /**
     * 备份分页查询
     *
     * @param pageNo
     * @param pageSize
     * @param filter
     * @return
     */
    @Override
    public PageInfo<List<BackupVO>> listBackup(int pageNo, int pageSize, String instanceId, String filter) {
        Specification<BackupPO> specification = new Specification<BackupPO>() {
            @Override
            public Predicate toPredicate(Root<BackupPO> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                Predicate deletedPredicate = criteriaBuilder.equal(root.get("isDeleted"), false);
                Predicate instanceIdPredicate = criteriaBuilder.equal(root.get("instanceId"), instanceId);
                Predicate resultPredicate;
                if (!StringUtils.isEmpty(filter)) {
                    Predicate idPredicate = criteriaBuilder.like(root.get("description"), "%" + filter + "%");
                    Predicate namePredicate = criteriaBuilder.like(root.get("name"), "%" + filter + "%");
                    Predicate filterPredicate = criteriaBuilder.or(idPredicate, namePredicate);
                    resultPredicate = criteriaBuilder.and(deletedPredicate, instanceIdPredicate, filterPredicate);
                } else {
                    resultPredicate = criteriaBuilder.and(deletedPredicate, instanceIdPredicate);
                }
                query.orderBy(criteriaBuilder.desc(root.get("createdAt")));
                return resultPredicate;
            }
        };

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<BackupPO> page = backupRepository.findAll(specification, pageable);
        List<BackupVO> backupVOS = new ArrayList<>();
        for (BackupPO backupPO : page.getContent()) {
            BackupVO backupVO = new BackupVO();
            BeanUtil.copyNotNullProperties(backupPO, backupVO);
            backupVOS.add(backupVO);
        }
        return PageInfo.<List<BackupVO>>builder().pageNo(pageNo).pageSize(pageSize).totalCount(page.getTotalElements()).data(backupVOS).build();
    }

    /**
     * 删除备份
     *
     * @param id        实例id
     * @param backupId  备份id
     * @return
     */
    @Override
    public ActionResponse deleteBackup(String id, String backupId) {
        instanceService.beforeOperateInstance(id);
        Optional<BackupPO> backupPOOptional = backupRepository.findById(backupId);
        if (!backupPOOptional.isPresent()) {
            throw new BackupException(BackupError.BACKUP_NOT_EXIST);
        }
        BackupPO backupPO = backupPOOptional.get();
        if (!InstanceAllowConstant.ALLOW_DELETE_BACKUP_STATUS.contains(backupPO.getStatus())) {
            throw new BackupException(BackupError.BACKUP_NOT_ALLOW_OPERATE);
        }
        backupPO.setStatus(BackupStatus.DELETING);
        backupPO.setUpdatedAt(CommonUtil.getUTCDate());
        BackupDTO backupDTO = new BackupDTO();
        BeanUtil.copyNotNullProperties(backupPO, backupDTO);
        backupRepository.save(backupPO);
        crService.deleteBackup(backupDTO);
        logger.info("[BackupServiceImpl.deleteBackup] delete backup job success. instanceId is {} backupid is {}", id, backupId);
        return ActionResponse.actionSuccess();
    }

    /**
     * 删除备份回调方法
     *
     * @param backupId
     * @param result
     */
    @Override
    public void deleteBackupCallback(String backupId, boolean result) {
        Optional<BackupPO> backupPOOptional = backupRepository.findById(backupId);
        if (!backupPOOptional.isPresent()) {
            logger.error("[BackupServiceImpl.deleteBackupCallback] backup is not exit. backupid is {}", backupId);
            return;
        }
        BackupPO backupPO = backupPOOptional.get();
        OperationStatus operationStatus;
        if (result) {
            backupPO.setStatus(BackupStatus.DELETED);
            backupPO.setIsDeleted(true);
            operationStatus = OperationStatus.SUCCESS;
        } else {
            backupPO.setStatus(BackupStatus.DELETE_FAILED);
            operationStatus = OperationStatus.FAILED;
        }
        backupPO.setDeletedAt(CommonUtil.getUTCDate());
        backupRepository.save(backupPO);
        logger.info("[BackupServiceImpl.deleteBackupCallback] delete backup callback success. backupid is {} status is {}", backupId, backupPO.getStatus());
        InstanceDTO instanceDTO = instanceService.getDTO(backupPO.getInstanceId());
        //String projectId = instanceService.getProjectIdByNamespace(instanceDTO.getClusterId(), instanceDTO.getNamespace());
        websocketService.sendMsgToUser(instanceDTO, OperationDTO.builder().name(OperationName.DELETE_BACKUP).status(operationStatus).build());
    }

    @Override
    public BackupDTO getBackupByBackupId(String backupId) {
        Optional<BackupPO> backupPOOptional = backupRepository.findById(backupId);
        if (!backupPOOptional.isPresent()) {
            logger.error("[BackupServiceImpl.getBackupByBackupId] backup is not exist. backupid is {}", backupId);
            return null;
        }
        BackupDTO backupDTO = new BackupDTO();
        BeanUtil.copyNotNullProperties(backupPOOptional.get(), backupDTO);
        return backupDTO;
    }

    @Override
    public void updateBackupFile(String backupId, String backupFile) {
        backupRepository.updateBackupFile(backupId, CommonUtil.getUTCDate(), backupFile);
    }

    /**
     * 通过实例id查询备份不分页清单
     *
     * @param instanceId
     * @return
     */
    @Override
    public List<BackupDTO> listBackupByInstanceId(String instanceId) {
        List<BackupDTO> backupDTOList = new ArrayList<>();
        List<BackupPO> backupPOList = backupRepository.listByInstanceId(instanceId);
        for (BackupPO backupPO : backupPOList) {
            BackupDTO backupDTO = new BackupDTO();
            BeanUtil.copyNotNullProperties(backupPO, backupDTO);
            backupDTOList.add(backupDTO);
        }
        return backupDTOList;
    }

    @Override
    public BackupDTO getBackupByCreateTime(String instanceId, Date createTime) {
        Optional<BackupPO> backupPOOptional = backupRepository.getBackupPOByCreatedAt(instanceId, createTime);
        if (backupPOOptional.isPresent()) {
            BackupDTO backupDTO = new BackupDTO();
            BeanUtil.copyNotNullProperties(backupPOOptional.get(), backupDTO);
            return backupDTO;
        } else {
            return null;
        }
    }

    /**
     * 初始化备份策略
     *
     * @param instanceId
     * @return
     */
    @Override
    public BackupPolicyPO initBackupPolicy(String instanceId) {
        // 初始化实例备份策略
        BackupPolicyPO backupPolicyPO = new BackupPolicyPO();
        backupPolicyPO.setCreatedAt(CommonUtil.getUTCDate());
        backupPolicyPO.setInstanceId(instanceId);
        backupPolicyRepository.save(backupPolicyPO);
        return backupPolicyPO;
    }

    @Override
    public BackupDTO getBackupByName(String instanceId, String name) {
        Optional<BackupPO> backupPOOptional = backupRepository.getBackupByName(instanceId, name);
        if (backupPOOptional.isPresent()) {
            BackupDTO backupDTO = new BackupDTO();
            BeanUtil.copyNotNullProperties(backupPOOptional.get(), backupDTO);
            return backupDTO;
        }
        return null;
    }

    @Override
    public void updateBackupStatus(String backupId, BackupStatus backupStatus){
        backupRepository.updateStatus(backupId, CommonUtil.getUTCDate(), backupStatus);
    }

    @Override
    public void updateBackupIsRestoring(String backupId, Boolean isRestoring) {
        backupRepository.updateIsRestoring(backupId, CommonUtil.getUTCDate(), isRestoring);
    }
}

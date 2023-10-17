package com.highgo.platform.operator.service;

import com.highgo.platform.apiserver.model.dto.BackupDTO;
import com.highgo.platform.apiserver.model.dto.BackupPolicyDTO;
import com.highgo.platform.apiserver.model.dto.InstanceDTO;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.CustomResource;

public interface CrService {

    /**
     *  创建CR，若cr已存在，则退出
     * @param instanceDTO
     * @return
     */
    public boolean createCr(InstanceDTO instanceDTO);

    /**
     * 更新CR,构建整个cr进行更新，若cr不存在则创建它
     * @param instanceDTO
     * @return 执行结果 成功true 失败false
     */
    public boolean applyCr(InstanceDTO instanceDTO);

    /**
     * 校验CR是否已存在
     * @param namespace 命名空间名称
     * @param crName CR名称
     * @return 存在-true 不存在-false
     */
    public boolean isCrExist(String clusterId, String namespace, String crName);

    /**
     * patch cr的resource字段(cpu memory)
     * @param instanceDTO
     * @return
     */
    public boolean patchCrResource(InstanceDTO instanceDTO);

    /**
     * patch cr的storage字段
     * @param instanceDTO
     * @return
     */
    public boolean patchCrStorage(InstanceDTO instanceDTO);

    /**
     * 删除CR
     * @param instanceDTO
     * @return 执行结果 成功true 失败false
     */
    public boolean deleteCr(InstanceDTO instanceDTO);

    /**
     * 删除实例的所有pod(重启实例时，删除pod进行重启)
     * @param instanceDTO
     * @return
     */
    public boolean deleteAllPod(InstanceDTO instanceDTO);

    /**
     * @description 重启数据库实例
     *
     * @param: instanceDTO
     * @return boolean
     * @author srk
     * @date 2023/10/13 16:28
     */
    boolean restartDatabase(InstanceDTO instanceDTO);

    public boolean nodeportSwitch(InstanceDTO instanceDTO);

    /**
     * 创建备份
     * @param backupDTO
     * @return
     */
    public boolean createBackup(BackupDTO backupDTO);

    /**
     * 更新自动备份策略
     * @param backupPolicyDTO
     * @return
     */
    public boolean applyBackupPolicy(BackupPolicyDTO backupPolicyDTO);

    /**
     * 删除备份
     * @param backupDTO
     * @return
     */
    public boolean deleteBackup(BackupDTO backupDTO);

    public boolean applyConfigParam(InstanceDTO instanceDTO);

    /**
     * 从CR中解析InstanceDTO
     * @param customResource
     * @return
     */
    public InstanceDTO getInstanceVOFromCR(CustomResource customResource);

    /**
     * 获取master 节点pod
     * @return
     */
    public Pod getMasterPod(InstanceDTO instanceDTO);

    /**
     * 恢复到当前实例
     * @param instanceDTO
     * @return
     */
    boolean restore(InstanceDTO instanceDTO);

    Integer getHgadminPort(String instanceId);
}

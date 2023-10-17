package com.highgo.platform.apiserver.service;

import com.highgo.cloud.enums.InstanceStatus;
import com.highgo.cloud.model.PageInfo;
import com.highgo.platform.apiserver.model.dto.InstanceDTO;
import com.highgo.platform.apiserver.model.dto.InstanceNetworkDTO;
import com.highgo.platform.apiserver.model.vo.request.*;
import com.highgo.platform.apiserver.model.vo.response.ActionResponse;
import com.highgo.platform.apiserver.model.vo.response.InstanceCountVO;
import com.highgo.platform.apiserver.model.vo.response.InstanceEventVO;
import com.highgo.platform.apiserver.model.vo.response.InstanceVO;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.storage.StorageClass;

import java.util.List;
import java.util.Map;

public interface InstanceService {

    /**
     * 校验参数并创建实例
     *
     * @param createInstanceParam
     * @return
     */
    public InstanceVO createInstance(CreateInstanceVO createInstanceParam);

    /**
     * 创建实例
     * @param instanceDTO
     * @return
     */
    public InstanceDTO createInstance(InstanceDTO instanceDTO);

    /**
     * 创建实例完成回调方法
     * @param result
     */
    public void createInstanceCallback(String instanceId,  List<InstanceNetworkDTO> networkDTOList, String originInstanceId, String originBackupId, boolean result);

    /**
     * 执行删除实例任务
     *
     * @param id
     * @return
     */
    public ActionResponse deleteInstance(String id);

    /**
     * 删除完成回调方法
     */
    public void deleteInstanceCallback(String id, boolean result);

    /**
     * 实例详情
     *
     * @param id 实例ID
     * @param id 项目Id
     * @return
     */
    public InstanceVO getVO(String id);

    /**
     * 查询实例DTO
     * @param id
     * @return
     */
    public InstanceDTO getDTO(String id);

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
    PageInfo<List<InstanceVO>> listByFilter(int userId, String filter, String clusterId, int pageNo, int pageSize);


    InstanceCountVO getInstanceCountByUser(String userId);

    /**
     * 统计所有项目下实例数量
     *
     * @return
     */
    public InstanceCountVO getInstanceCount();

    ///**
    // * 项目下所有实例列表
    // *
    // * @param projectId
    // * @return
    // */
    //@Deprecated
    //public List<InstanceVO> list(String projectId);

    /**
     * 查询所有项目下所有实例列表
     *
     * @return
     */
    public List<InstanceVO> list(String userId);

    /**
     * 修改实例描述
     *
     * @param id
     * @param modifyInstanceDescriptionParam
     * @return
     */
    public ActionResponse modifyInstanceDescription(String id, ModifyInstanceDescriptionVO modifyInstanceDescriptionParam);

    /**
     * 执行实例重启任务
     *
     * @param id        实例ID
     * @return
     */
    public ActionResponse restartInstance(String id);

    /**
     * 重启实例回调方法
     * @param id
     */
    public void restartInstanceCallback(String id, boolean result);

    /**
     * 执行实例规格变更任务 cpu memory
     *
     * @param id
     * @param modifyClassParam
     * @return
     */
    public ActionResponse modifyInstance(String id, ModifyClassVO modifyClassParam);

    /**
     * 规格变更完成回调方法
     * @param id
     */
    public void modifyInstanceCallback(String id, boolean result);

    /**
     * 执行磁盘扩容任务
     *
     * @param id
     * @param modifyStorageParam
     * @return
     */
    public ActionResponse extendInstance(String id, ModifyStorageVO modifyStorageParam);

    /**
     * 磁盘扩容完成回调方法
     * @param id
     * @param result
     */
    public void extendInstanceCallback(String id, boolean result);

    /**
     * 执行开启或关闭外网任务
     * @param id
     * @param modifySwitchVO
     * @return
     */
    public ActionResponse modifyNodeportSwitch(String id, ModifySwitchVO modifySwitchVO);

    /**
     * 开启外网完成回调方法
     */
    public void openNodeportSwitchCallback(String id, Integer nodeportRW, Integer nodeportRO, boolean result);

    /**
     * 关闭外网完成回调方法
     */
    public void closeNodeportSwitchCallback(String id, boolean result);

    /**
     *  操作实例前处理工作
     *  1 校验实例是否存在
     *  2 校验实例是否有权限
     *  3 返回一个实例内部传输对象DTO
     * @param instanceId
     * @return
     */
    public InstanceDTO beforeOperateInstance(String instanceId);

    /**
     * 校验实例名称是否唯一(同一集群同一命名空间内)
     * @param verifyInstanceNameVO
     * @return
     */
    public ActionResponse instanceNameUniqueCheck(VerifyInstanceNameVO verifyInstanceNameVO);

    /**
     * 获取实例事件信息
     * @param instanceId 实例id
     * @return
     */
    public InstanceEventVO getEvent(String instanceId);

    /**
     * 更新resource version
     * @param instanceId 实例id
     * @param resourceVersion
     */
    public void updateResourseVersion(String instanceId, long resourceVersion);

    /**
     * 获取resource version
     * @param instanceId
     * @return
     */
    public Long getResourceVersion(String instanceId);

    /**
     * 更新实例副本数量
     * @param nodeNum
     */
    public void updateNodeNum(String instanceId, int nodeNum);

    /**
     * 更新实例ststefulset事件信息
     * @param instanceId
     * @param stsEvent
     */
    public void updateStsEvent(String instanceId, String stsEvent);

    /**
     * 更新实例pod 事件信息
     * @param instanceId
     * @param podEvent
     */
    public void updatePodEvent(String instanceId, String podEvent);

    /**
     * 更新实例 ready节点数量
     * @param instanceId
     * @param nodeReadyNum
     */
    public void updateNodeReadyNum(String instanceId, int nodeReadyNum);

    /**
     * 更新实例事件信息
     * @param instanceId 实例id
     * @param readyNum ready的节点数量
     * @param stsEvent statefulset 事件信息
     * @param podEvent pod 事件信息
     */
    public void updateNodeEvent(String instanceId, int readyNum, String stsEvent, String podEvent);

    /**
     * 获取实例状态
     * @param instanceId
     * @return
     */
    public InstanceStatus getInstanceStatus(String instanceId);

    /**
     * 更新实例状态
     * @param instanceId 实例id
     * @param instanceStatus 实例状态
     */
    public void updateInstanceStatus(String instanceId, InstanceStatus instanceStatus);

    /**
     * 获取集群的存储信息
     * @param clusterId
     * @return
     */
    public List<StorageClass> getStorageClasses(String clusterId);

    /**
     * 获取实例master 节点pod信息
     * @return
     */
    public Pod getMasterPod(String instanceId);

    public String getProjectIdByNamespace(String clusterId, String namespace);

    /**
     * 开放接口， 处理扩展数据，入库等操作
     * @param instanceDTO
     * @return
     */
    public Map<String, Object> extrametaHandler(InstanceDTO instanceDTO);

    /**
     * 开放接口，实例详情中添加扩展属性，各产品自行处理
     * @param instanceId
     * @return
     */
    public Map<String, Object> getSpecialExtraMeta(String instanceId);

    ActionResponse getHgadminUrl(String instanceId);

    Integer createInstanceHgadminCallback(String instanceId);

    void updateInstanceCpuResource(String instanceId, Integer cpuResource);

    void updateInstanceMemoryResource(String instanceId, Integer memoryResource);
}

package com.highgo.platform.apiserver.controller;

import com.highgo.cloud.model.PageInfo;
import com.highgo.platform.apiserver.model.vo.request.*;
import com.highgo.platform.apiserver.model.vo.response.ActionResponse;
import com.highgo.platform.apiserver.model.vo.response.InstanceCountVO;
import com.highgo.platform.apiserver.model.vo.response.InstanceEventVO;
import com.highgo.platform.apiserver.model.vo.response.InstanceVO;
import com.highgo.platform.apiserver.service.impl.InstanceServiceImpl;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.storage.StorageClass;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("${common.request-path-prefix}/${common.version}")
public class InstanceController {
    private static final Logger logger = LoggerFactory.getLogger(InstanceController.class);

    @Autowired
    private InstanceServiceImpl instanceService;

    @ApiOperation(value = "新建实例 ", notes = "", tags = {"OpenAPI"})
    @RequestMapping(value = "/instances", method = RequestMethod.POST)
    public InstanceVO createInstance(@Validated @RequestBody CreateInstanceVO createInstanceVO) {
        logger.info("[InstanceController.createInstance] createInstanceVO is {}", createInstanceVO.toString());
        return instanceService.createInstance(createInstanceVO);
    }

    @ApiOperation(value = "删除实例", notes = "", tags = {"OpenAPI"})
    @RequestMapping(value = "/instances/{id}", method = RequestMethod.DELETE)
    public ActionResponse deleteInstance(@PathVariable String id) {
        logger.info("[InstanceController.deleteInstance] instanceid is {}", id);
        return instanceService.deleteInstance(id);
    }

    @ApiOperation(value = "实例详情", notes = "", tags = {"OpenAPI"})
    @RequestMapping(value = "/instances/{id}", method = RequestMethod.GET)
    public InstanceVO getInstance(@PathVariable String id) {
        return instanceService.getVO(id);
    }

    @ApiOperation(value = "实例分页", notes = "", tags = {"OpenAPI"})
    @RequestMapping(value = "/instances/{userId}/{pageNo}/{pageSize}", method = RequestMethod.GET)
    public PageInfo<List<InstanceVO>> listInstance(@PathVariable("userId") int userId,
                                                   @PathVariable("pageNo") int pageNo,
                                                   @PathVariable("pageSize") int pageSize,
                                                   @RequestParam(value = "clusterId") String clusterId,
                                                   @RequestParam(value = "filter") String filter) {
        return instanceService.listByFilter(userId, filter, clusterId, pageNo, pageSize);
    }

    @ApiOperation(value = "修改实例描述", notes = "", tags = {"OpenAPI"})
    @RequestMapping(value = "/instances/{id}/description", method = RequestMethod.PATCH)
    public ActionResponse modifyDescription(@PathVariable String id, @RequestBody @Validated ModifyInstanceDescriptionVO modifyInstanceDescriptionParam) {
        logger.info("[InstanceController.modifyDescription] instanceId is {}, modifyInstanceDescriptionParam is {}", id, modifyInstanceDescriptionParam.toString());
        return instanceService.modifyInstanceDescription(id, modifyInstanceDescriptionParam);
    }

    @ApiOperation(value = "重启实例", notes = "", tags = {"OpenAPI"})
    @RequestMapping(value = "/instances/{id}/action/restart", method = RequestMethod.POST)
    public ActionResponse restartInstance(@PathVariable String id) {
        logger.info("[InstanceController.restartInstance] restartInstance is {}", id);
        return instanceService.restartInstance(id);
    }

    @ApiOperation(value = "规格变更", notes = "", tags = {"OpenAPI"})
    @RequestMapping(value = "/instances/{id}/action/modify-spec", method = RequestMethod.POST)
    public ActionResponse modifyInstance(@Validated @PathVariable String id, @RequestBody ModifyClassVO modifyClassParam) {
        logger.info("[InstanceController.modifyInstance] instanceId is {},modifyClassParam is {}", id, modifyClassParam.toString());
        return instanceService.modifyInstance(id, modifyClassParam);
    }

    @ApiOperation(value = "磁盘扩容", notes = "", tags = {"OpenAPI"})
    @RequestMapping(value = "/instances/{id}/action/storage", method = RequestMethod.POST)
    public ActionResponse extendInstance(@Validated @PathVariable String id, @RequestBody ModifyStorageVO modifyStorageParam) {
        logger.info("[InstanceController.extendInstance] instanceId is {}, modifyStorageParam is {}", id, modifyStorageParam.toString());
        return instanceService.extendInstance(id, modifyStorageParam);
    }

    ///**
    // * 统计实例数量
    // *
    // * @return
    // */
    //@ApiOperation(value = "实例数量统计", notes = "", tags = {"OpenAPI"})
    //@RequestMapping(value = "/instances/action/count", method = RequestMethod.GET)
    //public InstanceCountVO countInstance() {
    //    return instanceService.getInstanceCount();
    //}
    /**
     * 统计实例数量
     *
     * @return
     */
    @ApiOperation(value = "实例数量统计", notes = "", tags = {"OpenAPI"})
    @RequestMapping(value = "/instances/action/count/{userId}", method = RequestMethod.GET)
    public InstanceCountVO countInstance(@PathVariable("userId") String userId) {
        return instanceService.getInstanceCountByUser(userId);
    }

    /**
     * 所有实例列表
     *
     * @return
     */
    @ApiOperation(value = "实例列表", notes = "", tags = {"OpenAPI"})
    @RequestMapping(value = "/instances/list/{userId}", method = RequestMethod.GET)
    public List<InstanceVO> list(@PathVariable String userId) {
            return instanceService.list(userId);
    }

    @ApiOperation(value = "外网开关", notes = "", tags = {"OpenAPI"})
    @RequestMapping(value = "/instances/{id}/action/nodeport-switch", method = RequestMethod.POST)
    public ActionResponse nodeportSwitch(@Validated @PathVariable String id, @RequestBody ModifySwitchVO modifySwitchVO) {
        logger.info("[InstanceController.nodeportSwitch] instanceId is {}, modifySwitchVO is {}", id, modifySwitchVO.toString());
        return instanceService.modifyNodeportSwitch(id, modifySwitchVO);
    }

    @ApiOperation(value = "名称唯一性校验", notes = "", tags = {"OpenAPI"})
    @RequestMapping(value = "/instances/action/verify-name", method = RequestMethod.POST)
    public ActionResponse instanceNameUniqueCheck(@RequestBody VerifyInstanceNameVO verifyInstanceNameVO) {
        return instanceService.instanceNameUniqueCheck(verifyInstanceNameVO);
    }

    @ApiOperation(value = "获取实例事件信息", notes = "", tags = {"OpenAPI"})
    @RequestMapping(value = "/instances/{id}/events", method = RequestMethod.GET)
    public InstanceEventVO getEvent(@Validated @PathVariable String id) {
        return instanceService.getEvent(id);
    }

    @ApiOperation(value = "获取存储类型", notes = "", tags = {"OpenAPI"})
    @RequestMapping(value = "/storage-class", method = RequestMethod.GET)
    public List<StorageClass> getStorageClass(@RequestParam(value = "clusterId") String clusterId) {
        return instanceService.getStorageClasses(clusterId);
    }

    @ApiOperation(value = "获取实例主节点pod", notes = "", tags = {"OpenAPI"})
    @RequestMapping(value = "/instances/{id}/master-pod", method = RequestMethod.GET)
    public Pod getMasterPod(@Validated @PathVariable("id") String id) {
        return instanceService.getMasterPod(id);
    }

    @ApiOperation(value = "获取hgadmin连接信息", notes = "", tags = {"OpenAPI"})
    @RequestMapping(value = "/instances/{id}/hgadmin", method = RequestMethod.GET)
    public ActionResponse getHgadminUrl(@Validated @PathVariable("id") String id) {
        return instanceService.getHgadminUrl(id);
    }
}

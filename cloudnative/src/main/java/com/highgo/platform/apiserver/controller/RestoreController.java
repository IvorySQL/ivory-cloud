package com.highgo.platform.apiserver.controller;

import com.highgo.platform.apiserver.service.RestoreService;
import com.highgo.platform.apiserver.model.vo.request.RestoreInstanceVO;
import com.highgo.platform.apiserver.model.vo.response.ActionResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author lucunqiao
 * @date 2023/1/5
 */
@Validated
@RestController
@RequestMapping("${common.request-path-prefix}/${common.version}")
@Api(value = "数据库恢复", tags = { "数据库备份恢复接口" })
public class RestoreController {
    private static final Logger logger = LoggerFactory.getLogger(RestoreController.class);

    @Autowired
    private RestoreService restoreService;


    @ApiOperation(value = "恢复当前实例", notes = "", tags = {"OpenAPI"})
    @RequestMapping(value = "/instances/{id}/restore", method = RequestMethod.POST)
    public ActionResponse restoreInstance(@Validated @PathVariable String id, @RequestBody RestoreInstanceVO restoreInstanceVO) {
        logger.info("[RestoreController.restoreInstance] instanceid is {} restoreInstanceVO is {}", id, restoreInstanceVO.toString());
        return restoreService.restoreInstance(id, restoreInstanceVO);
    }




}

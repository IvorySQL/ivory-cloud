package com.highgo.platform.apiserver.controller;

import com.highgo.platform.apiserver.model.vo.request.DatabaseUserVO;
import com.highgo.platform.apiserver.model.vo.request.ResetPasswordVO;
import com.highgo.platform.apiserver.model.vo.response.ActionResponse;
import com.highgo.platform.apiserver.service.DBUserService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("${common.request-path-prefix}/${common.version}")
public class DBUserController {
    private static final Logger logger = LoggerFactory.getLogger(DBUserController.class);

    @Resource
    private DBUserService DBUserService;


    @ApiOperation(value = "重置用户密码", notes = "", tags = {"OpenAPI"})
    @RequestMapping(value = "/instances/{id}/users/{username}/reset-password", method = RequestMethod.PUT)
    public ActionResponse resetPassword(@PathVariable String id, @PathVariable String username, @RequestBody ResetPasswordVO resetPasswordVO ) {
        return DBUserService.resetPassword(id, username, resetPasswordVO);
    }

    @ApiOperation(value = "锁定/解锁用户", notes = "")
    @RequestMapping(value = "/instances/{id}/users/{accountName}/lock-account", method = RequestMethod.POST)
    public ActionResponse lockDbUser(@PathVariable String id, @PathVariable String accountName, @RequestBody @Validated Map<String,String> lock) {
        return DBUserService.lockDbUser(id, accountName, lock.get("lock"));
    }

    @ApiOperation(value = "新建用户", notes = "")
    @RequestMapping(value = "/instances/{id}/users", method = RequestMethod.POST)
    public ActionResponse createDbUser(@PathVariable String id, @RequestBody @Validated DatabaseUserVO dbUser) {
        return DBUserService.createDBUser(id, dbUser);
    }


    @ApiOperation(value = "删除用户", notes = "")
    @RequestMapping(value = "/instances/{id}/users/{userName}", method = RequestMethod.DELETE)
    public ActionResponse deleteDbUser(@PathVariable String id, @PathVariable String userName) {
        return DBUserService.deleteDbUser(id, userName);
    }

    @ApiOperation(value = "用户列表", notes = "")
    @RequestMapping(value = "/instances/{id}/users", method = RequestMethod.GET)
    public List<DatabaseUserVO> listDbUsers(@PathVariable String id) {
        return DBUserService.listDbUsers(id);
    }
}

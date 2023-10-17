package com.highgo.platform.apiserver.controller;

import com.highgo.platform.apiserver.model.vo.request.DatabaseVO;
import com.highgo.platform.apiserver.model.vo.response.ActionResponse;
import com.highgo.platform.apiserver.service.DBDatabaseService;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author lucunqiao
 * @date 2022/12/14
 */
@Validated
@RestController
@RequestMapping("${common.request-path-prefix}/${common.version}")
public class DBDatabaseController {

    @Resource
    private DBDatabaseService DBDatabaseService;

    @ApiOperation(value = "新建数据库", notes = "")
    @RequestMapping(value = "/instances/{id}/dbs", method = RequestMethod.POST)
    public ActionResponse createDatabase(@PathVariable String id, @RequestBody @Validated DatabaseVO databaseVO) {
        return DBDatabaseService.createDatabase(id, databaseVO);
    }

    @ApiOperation(value = "删除数据库", notes = "")
    @RequestMapping(value = "/instances/{id}/dbs/{dbName}", method = RequestMethod.DELETE)
    public ActionResponse deleteDatabase(@PathVariable String id, @PathVariable String dbName) {
        return DBDatabaseService.deleteDatabase(id, dbName);
    }

    @ApiOperation(value = "数据库列表", notes = "")
    @RequestMapping(value = "/instances/{id}/dbs", method = RequestMethod.GET)
    public List<DatabaseVO> listDatabase(@PathVariable String id) {
        return DBDatabaseService.listDatabases(id);
    }

}



package com.highgo.platform.apiserver.controller;

import com.highgo.cloud.enums.BackupMode;
import com.highgo.cloud.model.PageInfo;
import com.highgo.platform.apiserver.model.vo.request.CreateBackupVO;
import com.highgo.platform.apiserver.model.vo.request.ModifyAutoBackupSwitchVO;
import com.highgo.platform.apiserver.model.vo.request.ModifyBackupPolicyVO;
import com.highgo.platform.apiserver.model.vo.response.ActionResponse;
import com.highgo.platform.apiserver.model.vo.response.BackupPolicyVO;
import com.highgo.platform.apiserver.model.vo.response.BackupVO;
import com.highgo.platform.apiserver.service.impl.BackupServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author lucunqiao
 * @date 2023/1/5
 */
@Validated
@RestController
@RequestMapping("${common.request-path-prefix}/${common.version}")
@Api(value = "数据库备份", tags = { "数据库备份恢复接口" })
public class BackupController {
    private static final Logger logger = LoggerFactory.getLogger(BackupController.class);

    @Autowired
    private BackupServiceImpl backupService;
    @ApiOperation(value = "创建备份", notes = "", tags = {"OpenAPI"})
    @RequestMapping(value = "/instances/{id}/backups", method = RequestMethod.POST)
    public BackupVO createBackup(@Validated @PathVariable String id, @RequestBody CreateBackupVO createBackupParam) {
        logger.info("[BackupController.createBackup] instanceid is {} createBackupParam is {}", id, createBackupParam.toString());
        createBackupParam.setBackupMode(BackupMode.INCREMENTAL);
        return backupService.createBackup(id, createBackupParam);
    }

    @ApiOperation(value = "删除备份", notes = "", tags = {"OpenAPI"})
    @RequestMapping(value = "/instances/{id}/backups/{backupId}", method = RequestMethod.DELETE)
    public ActionResponse deleteBackup(@Validated @PathVariable String id, @Validated @PathVariable String backupId){
        logger.info("[BackupController.deleteBackup] instanceid is {} backupid is {}", id, backupId);
        return backupService.deleteBackup(id, backupId);
    }

    @ApiOperation(value = "查询备份策略", notes = "", tags = {"OpenAPI"})
    @RequestMapping(value = "/instances/{id}/backups/backup-policy", method = RequestMethod.GET)
    public BackupPolicyVO getBackupPolicy(@Validated @PathVariable String id) {
        return backupService.getBackupPolicy(id);
    }

    @ApiOperation(value = "修改备份策略", notes = "", tags = {"OpenAPI"})
    @RequestMapping(value = "/instances/{id}/backups/action/modify-backup-policy", method = RequestMethod.PUT)
    public BackupPolicyVO modifyBackupPolicy(@Validated @PathVariable String id, @RequestBody ModifyBackupPolicyVO modifyBackupPolicyParam) {
        logger.info("[BackupController.modifyBackupPolicy] instanceid is {} modifyBackupPolicyParam is {}", id, modifyBackupPolicyParam.toString());
        return backupService.modifyBackupPolicy(id, modifyBackupPolicyParam);
    }

    @ApiOperation(value = "设置自动备份开关", notes = "", tags = {"OpenAPI"})
    @RequestMapping(value = "/instances/{id}/backups/action/switch-auto-backup", method = RequestMethod.POST)
    public ActionResponse modifyBackupSwitch(@Validated @PathVariable String id, @RequestBody ModifyAutoBackupSwitchVO modifyAutoBackupSwitchParam) {
        logger.info("[BackupController.modifyBackupSwitch] instanceid is {} modifyAutoBackupSwitchParam is {}", id, modifyAutoBackupSwitchParam.toString());
        return backupService.modifyBackupSwitch(id, modifyAutoBackupSwitchParam);
    }

    @ApiOperation(value = "备份分页", notes = "", tags = {"OpenAPI"})
    @RequestMapping(value = "/instances/{id}/backups/{pageNo}/{pageSize}", method = RequestMethod.GET)
    public PageInfo<List<BackupVO>> listBackup(@PathVariable int pageNo, @PathVariable int pageSize,
                                               @PathVariable String id, @RequestParam String filter) {
        return backupService.listBackup(pageNo, pageSize, id, filter);
    }


}

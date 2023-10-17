package com.highgo.platform.operator.cr.bean.status;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * @author lucunqiao
 * @date 2023/1/18
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PgbackrestStatus {

    private ManualBackupStatus manualBackup;

    private List<ScheduledBackupsStatus> scheduledBackups;

    private List<Repo> repos;

    private RepoHost repoHost;

    private RestoreStatus restore;
}

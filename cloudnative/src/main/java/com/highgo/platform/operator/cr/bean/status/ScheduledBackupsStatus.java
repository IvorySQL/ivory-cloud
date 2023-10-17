package com.highgo.platform.operator.cr.bean.status;

import lombok.Data;

@Data
public class ScheduledBackupsStatus {

    private Integer active;

    private String completionTime;

    private String cronJobName;

    private Integer failed;

    private String repo;

    private String startTime;

    private Integer succeeded;

    private String type;
}

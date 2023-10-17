package com.highgo.platform.operator.cr.bean.status;

import lombok.Data;

@Data
public class ManualBackupStatus {

    private Integer active;

    private String completionTime;

    private Boolean finished;

    /**
     *  manual backup id
     */
    private String id;

    private String startTime;

    private Integer succeeded;

    private Integer failed;

}

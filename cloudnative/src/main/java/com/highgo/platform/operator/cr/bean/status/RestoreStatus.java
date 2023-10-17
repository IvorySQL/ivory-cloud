package com.highgo.platform.operator.cr.bean.status;

import lombok.Data;

/**
 * @author lucunqiao
 * @date 2023/1/18
 */
@Data
public class RestoreStatus {

    private Integer active;

    private String completionTime;

    private Integer failed;

    private Boolean finished;

    private String id;

    private String startTime;

    private Integer succeeded;
}

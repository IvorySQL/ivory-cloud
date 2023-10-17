package com.highgo.platform.apiserver.model.dto;

import com.highgo.cloud.enums.OperationName;
import com.highgo.cloud.enums.OperationStatus;
import lombok.Builder;

import java.io.Serializable;

@Builder
public class OperationDTO implements Serializable {


    /**
     * 操作步骤名称
     */
    private OperationName name;

    /**
     * 操作步骤状态
     */
    private OperationStatus status;

    public OperationName getName() {
        return name;
    }

    public void setName(OperationName name) {
        this.name = name;
    }

    public OperationStatus getStatus() {
        return status;
    }

    public void setStatus(OperationStatus status) {
        this.status = status;
    }
}

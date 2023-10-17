package com.highgo.platform.exception;

import com.highgo.platform.errorcode.BaseError;

/**
 * @author: highgo-lucunqiao
 * @date: 2023/3/3 10:27
 * @Description: k8s集群相关exception
 */
public class ClusterException extends BadRequestException {

    private static final long serialVersionUID = -1666916136000L;


    public ClusterException(BaseError error, Object...args) {
        super(error.code(), error.message(args));
    }

    public ClusterException(BaseError errorCode) {
        super(errorCode.code(), errorCode.message());
    }
}

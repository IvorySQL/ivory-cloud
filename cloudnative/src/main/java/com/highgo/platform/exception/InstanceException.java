package com.highgo.platform.exception;

import com.highgo.platform.errorcode.BaseError;


public class InstanceException extends BadRequestException {

    /**
     *
     */
    private static final long serialVersionUID = -1666916136000L;


    public InstanceException(BaseError error, Object...args) {
        super(error.code(), error.message(args));
    }

    public InstanceException(BaseError errorCode) {
        super(errorCode.code(), errorCode.message());
    }

}

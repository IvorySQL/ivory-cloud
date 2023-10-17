package com.highgo.platform.exception;

import com.highgo.platform.errorcode.BaseError;


public class CommonException extends BadRequestException {

    /**
     *
     */
    private static final long serialVersionUID = -1666916136000L;

    public CommonException(BaseError error, Object...args) {
        super(error.code(), error.message(args));
    }

    public CommonException(BaseError errorCode) {
        super(errorCode.code(), errorCode.message());
    }

}

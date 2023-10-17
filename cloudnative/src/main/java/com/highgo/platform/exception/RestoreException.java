package com.highgo.platform.exception;

import com.highgo.platform.errorcode.BaseError;


public class RestoreException extends BadRequestException {

    /**
     *
     */

    public RestoreException(BaseError error, Object...args) {
        super(error.code(), error.message(args));
    }

    public RestoreException(BaseError errorCode) {
        super(errorCode.code(), errorCode.message());
    }

}

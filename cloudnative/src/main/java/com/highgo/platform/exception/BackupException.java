package com.highgo.platform.exception;

import com.highgo.platform.errorcode.BaseError;


public class BackupException extends BadRequestException {

    /**
     *
     */

    public BackupException(BaseError error, Object...args) {
        super(error.code(), error.message(args));
    }

    public BackupException(BaseError errorCode) {
        super(errorCode.code(), errorCode.message());
    }

}

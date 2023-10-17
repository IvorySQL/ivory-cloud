package com.highgo.platform.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends BaseException {

    /**
     *
     */
    private static final long serialVersionUID = 508205654241104087L;


    private static int STATUS_CODE = HttpStatus.BAD_REQUEST.value();

    public BadRequestException(String code, String message) {

        //super(STATUS_CODE, code, message, ConnectorUtils.getRequestId());
        super(STATUS_CODE, code, message,"requestId");
    }


}

package com.highgo.platform.exception;

import com.highgo.platform.errorcode.BaseError;

/**
 * @author: highgo-lucunqiao
 * @date: 2023/4/23 10:55
 * @Description: 自动弹性伸缩异常
 */
public class AutoScalingException extends BadRequestException{

    public AutoScalingException(BaseError error, Object...args) {
        super(error.code(), error.message(args));
    }

    public AutoScalingException(BaseError errorCode) {
        super(errorCode.code(), errorCode.message());
    }
}

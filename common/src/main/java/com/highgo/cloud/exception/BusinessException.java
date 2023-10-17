package com.highgo.cloud.exception;

import lombok.NoArgsConstructor;

/**
 * BusinessException
 * 业务自定定义运行时异常
 * @author renlizou
 */
@NoArgsConstructor
public class BusinessException extends RuntimeException {
    //private static final long serialVersionUID = 1L;
    //
    //    public BusinessException(String message) {
    //        super(message);
    //    }
    //
    //    public BusinessException(Throwable cause) {
    //        super(cause);
    //    }
    //
    //    public BusinessException(String message, Throwable cause) {
    //        super(message, cause);
    //    }
    //
    //    public BusinessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    //        super(message, cause, enableSuppression, writableStackTrace);
    //    }

    private static final long serialVersionUID = 1L;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(Throwable cause) {
        super(cause);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}

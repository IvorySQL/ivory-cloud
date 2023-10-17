package com.highgo.cloud.exception;

import lombok.NoArgsConstructor;

/**
 * @author lucunqiao
 * @date 2022/5/17
 */

@NoArgsConstructor
public class HgJdbcException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public HgJdbcException(String message) {
        super(message);
    }

    public HgJdbcException(Throwable cause) {
        super(cause);
    }

    public HgJdbcException(String message, Throwable cause) {
        super(message, cause);
    }

    public HgJdbcException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}

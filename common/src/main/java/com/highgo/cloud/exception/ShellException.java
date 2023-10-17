package com.highgo.cloud.exception;

import lombok.NoArgsConstructor;

/**
 * ShellException
 * 脚本执行自定定义运行时异常
 * @author renlizou
 */
@NoArgsConstructor
public class ShellException extends RuntimeException {
    //private static final long serialVersionUID = 1L;
    //
    //    public ShellException(String message) {
    //        super(message);
    //    }
    //
    //    public ShellException(Throwable cause) {
    //        super(cause);
    //    }
    //
    //    public ShellException(String message, Throwable cause) {
    //        super(message, cause);
    //    }
    //
    //    public ShellException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    //        super(message, cause, enableSuppression, writableStackTrace);
    //    }

    private static final long serialVersionUID = 1L;

    public ShellException(String message) {
        super(message);
    }

    public ShellException(Throwable cause) {
        super(cause);
    }

    public ShellException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShellException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

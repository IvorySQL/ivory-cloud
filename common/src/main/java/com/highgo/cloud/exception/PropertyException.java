package com.highgo.cloud.exception; /**
 *
 */

/**
 * @author cww<br>
 * @version 1.0
 * 2012-12-18 下午1:26:28<br>
 */
public class PropertyException extends RuntimeException {
    private static final String MESSAGE = "org.apache.commons.beanutils.PropertyUtils error!";
    /**
     *
     */
    public PropertyException() {
        super(MESSAGE);
    }

    /**
     * @param message
     */
    public PropertyException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public PropertyException(Throwable cause) {
        super(MESSAGE, cause);
    }

    /**
     * @param message
     * @param cause
     */
    public PropertyException(String message, Throwable cause) {
        super(message, cause);
    }

}
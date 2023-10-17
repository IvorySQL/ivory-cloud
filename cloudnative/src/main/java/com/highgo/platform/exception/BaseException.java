package com.highgo.platform.exception;


public class BaseException extends RuntimeException {


    public static final String BAD_REQUEST_CODE = ".999400";
    public static final String BAD_REQUEST_MESSAGE_CODE = "common.error.bad_request.message";

    public static final String NOT_FOUND_CODE = ".999404";
    public static final String NOT_FOUND_MESSAGE_CODE = "common.error.not_found.message";


    public static final String INTERNAL_SERVER_SUFFIX_ERROR_CODE = ".999500";
    public static final String INTERNAL_SERVER_ERROR_MESSAGE_CODE = "common.error.internal_server.message";

    public static final String SERVICE_UNAVAILABLE_CODE = ".999503";
    public static final String SERVICE_UNAVAILABLE_MESSAGE_CODE = "common.error.service_unavailable.message";

    public static final String METHOD_NOT_ALLOWED_CODE = ".999405";

    public static final String UNSUPPORTED_MEDIA_TYPE_CODE = ".999415";

    public static final String NOT_ACCEPTABLE_CODE = ".999406";
    public static final String NOT_ACCEPTABLE_MESSAGE_CODE = "common.error.not_acceptable.message";
    /**
     *
     */
    private static final long serialVersionUID = 5629265005450565933L;

    private int statusCode;
    private String code;
    private String message;
    private String requestId;

    public BaseException(int statusCode, String code, String message, String requestId) {
        super(message);
        this.statusCode = statusCode;
        this.message = message;
        this.requestId = requestId;
        this.code = code;
    }

    /**
     * @return the statusCode
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * @param statusCode the statusCode to set
     */
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the requestId
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * @param requestId the requestId to set
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

}

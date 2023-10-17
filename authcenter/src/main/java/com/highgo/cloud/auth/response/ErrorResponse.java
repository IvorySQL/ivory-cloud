package com.highgo.cloud.auth.response;

/**
 * Created by pengq on 2019/9/17 9:09.
 */
public class ErrorResponse extends GenericResponse {
    public ErrorResponse(GlobalResponseCode globalResponseCode) {
        super(globalResponseCode.getCode(), globalResponseCode.getMessage());
    }

    public ErrorResponse(int code, String message) {
        super(code, message);
    }

}

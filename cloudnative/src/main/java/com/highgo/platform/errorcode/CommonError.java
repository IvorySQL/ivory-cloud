package com.highgo.platform.errorcode;


public enum CommonError implements BaseError{

    COMMON_ERROR("200.000001", "instance..nternal.error"); // 内部错误


    private String code;
    private String message;

    CommonError(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String message(Object... args) {
        return this.message;
    }

    @Override
    public String message() {
        return this.message;
    }

    @Override
    public String code() {
        return code;
    }

}

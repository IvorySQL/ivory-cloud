package com.highgo.platform.errorcode;


public enum UserError implements BaseError{

    DUPLICATE_NAME("200.003001", "instance.user.duplicate_name"),
    USER_NOT_EXIST("200.003002", "instance.user.not_exist");

    private String code;
    private String message;

    UserError(String code, String message) {
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

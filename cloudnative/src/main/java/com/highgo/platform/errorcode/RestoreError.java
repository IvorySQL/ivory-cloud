package com.highgo.platform.errorcode;


public enum RestoreError implements BaseError{

    RESTORE_TIME_ERROR("200.006001", "restore.time.invalid");



    private String code;
    private String message;

    RestoreError(String code, String message) {
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

package com.highgo.platform.errorcode;


public interface BaseError {
    public String code();
    public String message(Object...args);
    public String message();
}

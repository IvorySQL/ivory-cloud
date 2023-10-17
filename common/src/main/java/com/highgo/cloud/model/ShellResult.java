package com.highgo.cloud.model;

public class ShellResult {
    public String message;
    public int returnCode;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }

    @Override
    public String toString() {
        return "ShellResult{" +
                "message='" + message + '\'' +
                ", returnCode=" + returnCode +
                '}';
    }
}

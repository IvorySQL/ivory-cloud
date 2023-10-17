package com.highgo.platform.errorcode;

/**
 * @author: highgo-lucunqiao
 * @date: 2023/4/23 10:59
 * @Description: 自动弹性伸缩错误码
 */
public enum AutoScalingError implements BaseError {
    AUTOSCALINGSWITCH_NOT_EXIST("200.007001", "autoscalingswitch.not.exist"),
    USER_MONITOR_NOT_EXIST("200.007002", "user.monitoring.information.not.exist"),
    CONFIG_ALERTMANAGER_FAILED("200.007003", "config.alertmanager.failed");



    private String code;
    private String message;

    AutoScalingError(String code, String message) {
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

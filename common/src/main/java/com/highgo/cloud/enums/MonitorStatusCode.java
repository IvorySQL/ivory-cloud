package com.highgo.cloud.enums;

public enum MonitorStatusCode {
    ACTIVE("active", 0), FAILED("failed", 1), BUILDING("building", 2), UNINSTALL("uninstall", 3), ERROR("error", -1);
    private String name;
    private int code;

    MonitorStatusCode(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public static String getName(int code) {
        for (MonitorStatusCode m : MonitorStatusCode.values()) {
            if (m.getCode() == code) {
                return m.name;
            }
        }
        return null;
    }

    // get set 方法
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}

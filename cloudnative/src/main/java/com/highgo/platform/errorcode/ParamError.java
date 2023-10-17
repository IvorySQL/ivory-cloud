package com.highgo.platform.errorcode;

public enum ParamError implements BaseError{

    RESTORE_("200.005001", "instance.create.duplicate_name"), // 实例重名
    INSTANCE_NOT_EXIST("200.005002", "instance.not_exist"),     // 实例不存在
    INSTANCE_NOT_AUTH("200.005003","instance.not_authority"),  // 实例无操作权限
    INSTANCE_NOT_ALLOW_OPERATE("200.005.004", "instance.not_allow_operate"), // 当前状态不允许操作
    INSTANCE_NOT_ALLOW_DEMOTE("200.005.005", "instance.not_allow_demote"), // 实例规格不允许降级，只能升级
    INSTANCE_NOT_ALLOW_SHRINKAGE("200.005.006", "instance.not_allow_shrinkage"), // 实例存储不允许缩容，只能扩容
    INSTANCE_NO_CHANGE("200.005.007", "instance.no_change"); // 实例变更参数与当前一直，无需变更

    private String code;
    private String message;

    ParamError(String code, String message) {
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

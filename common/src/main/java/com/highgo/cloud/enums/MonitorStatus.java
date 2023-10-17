package com.highgo.cloud.enums;

public enum MonitorStatus {

    /**
     * 创建中
     */
    CREATING
    /**
     * 创建失败
     */
    , CREATE_FAILED
    /**
     * 运行中
     */
    , RUNNING
    /**
     * 重启中
     */
    , RESTARTING

    /**
     * 删除中
     */
    , DELETING
    /**
     * 已删除
     */
    ,DELETED
    /**
     * 删除失败
     */
    , DELETE_FAILED
    /**
     * 更配/升级规格中
     */
    , UPGRADING
    /**
     * 更配/升级规格失败
     */
    , UPGRADE_FLAVOR_FAILED
    /**
     * 扩容中
     */
    , EXTENDING
    /**
     * 扩容失败
     */
    , EXTEND_STORAGE_FAILED
    /**
     * 重启失败
     */
    , RESTART_FAILED
    /**
     * 更新配置中
     */
    , CONFIG_CHANGING
    /**
     * 更新配置失败
     */
    , CONFIG_CHANGE_FAILED
    /**
     * 异常
     */
    , ERROR

    /**
     * 清空数据失败
     */
    ,PURGE_FAILED
    /**
     * 清空数据中
     */
    ,PURGING

}

package com.highgo.cloud.enums;

public enum OperationName {
    /**
     * 创建实例
     */
    CREATE_INSTANCE

    /**
     * 删除实例
     */
    ,DELETE_INSTANCE

    /**
     * 实例规格变更
     */
    ,MODIFY_INSTANCE

    /**
     * 实例存储扩容
     */
    ,EXTEND_STORAGE
    /**
     * 实例重启
     */
    ,RESTART_INSTANCE
    /**
     * 实例开启外网
     */
    ,OPEN_NODEPORT
    /**
     * 实例关闭外网
     */
    ,CLOSE_NODEPORT
    /**
     * 创建备份
     */
    , CREATE_BACKUP
    /**
     * 删除备份
     */
    , DELETE_BACKUP
    /**
     * 恢复备份
     */
    , RESTORE_BACKUP
    /**
     * 清空数据
     */
    ,PURGE
    /**
     * 参数变更
     */
    ,CONFIG_CHANGE
}

package com.highgo.cloud.enums;

public enum BackupStatus {
    /**
     * 备份中
     */
    PROCESSING

    /**
     * 备份完成
     */
    , COMPLETED

    /**
     * 备份失败
     */
    , FAILED

    /**
     * 删除中
     */
    , DELETING

    /**
     * 已删除
     */
    , DELETED
    /**
     * 删除失败
     */
    ,DELETE_FAILED
    /**
     * 恢复成功
     */
    ,RESTORED
    /**
     * 恢复失败
     */
    ,RESTORE_FAILED

    /**
     * 恢复中
     */
    , RESTORING
}

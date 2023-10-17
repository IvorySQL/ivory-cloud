package com.highgo.cloud.enums;

/**
 * @author: highgo-lucunqiao
 * @date: 2023/7/25 15:26
 * @Description: db_back工具备份记录状态
 */
public enum DbBackStatus {
    //from db_back code:
    //typedef enum Backupstatus
    RUNNING,
    ERROR,
    OK,
    DONE,
    DELETING,
    CORRUPT,
    DELETED,
    INVALID
}

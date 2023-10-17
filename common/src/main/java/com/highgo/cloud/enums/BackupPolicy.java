package com.highgo.cloud.enums;

import lombok.Getter;

/**
 *  策略类型
 */
@Getter
public enum BackupPolicy {

    RETENTION("RETENTION","保留策略"),
    BACKUP("BACKUP","备份策略");

    /**
     * code
     */
    private String code;

    /**
     * content
     */
    private String content;

    BackupPolicy(String code,String content){
        this.code = code;
        this.content = content;
    }

}

package com.highgo.cloud.enums;

import lombok.Getter;

/**
 *  策略变更类型
 */
@Getter
public enum BackupPolicyType {

    UPDATE("UPDATE","更新"),
    DELETE("DELETE","删除"),
    CREATE("CREATE","新增");

    /**
     * code
     */
    private String code;

    /**
     * content
     */
    private String content;

    BackupPolicyType(String code,String content){
        this.code = code;
        this.content = content;
    }

}

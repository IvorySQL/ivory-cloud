package com.highgo.cloud.enums;

public enum UserOption {

    /**
     * 账户权限
     */

    /**
     * 超级管理员
     */
    SUPERUSER

    /**
     * 创建数据库
     */
    , CREATEDB

    /**
     * 创建角色
     */
    , CREATEROLE

    /**
     *继承
     */
    , INHERIT

    /**
     * 登录
     */
    , LOGIN

    /**
     * 复制
     */
    , REPLICATION

    /**
     * 绕过RLS
     */
    , BYPASSRLS
    ,NOSUPERUSER
    ,NOCREATEDB
    ,NOCREATEROLE
    ,NOINHERIT
    ,NOLOGIN
    ,NOREPLICATION
    ,NOBYPASSRLS

}

package com.highgo.cloud.constant;

/**
 * @author: highgo-lucunqiao
 * @date: 2023/6/25 14:16
 * @Description: for saas
 */
public class SaasConstant {
    /**
     * SAAS 默认的数据库密码
     */
    public static final String SAAS_DB_DEFAULT_PASSWORD = "highgo";

    /**
     * 数据库类型：标准版
     */
    public static final int SAAS_POSTGRESQL_TYPE = 0;

    /**
     * 默认一次订阅一个数据库
     */
    public static final int SAAS_DB_COUNT = 1;

    /**
     * 默认磁盘大小
     */
    public static final int SAAS_DEFAULT_DISK_SIZE = 60;

    //数据库冻结
    public static final String DB_FROZEN = "frozen";
}

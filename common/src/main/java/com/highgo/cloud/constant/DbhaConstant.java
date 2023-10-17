package com.highgo.cloud.constant;

/**
 * dbha需要的常量
 * @author chushaolin
 *
 */
public class DbhaConstant {
	//TODO： chushaolin
    /**
     * dbha  主节点的value
     */
    public static final int PRIMARY_NODE = 0;

    /**
     * dbha  备节点的value
     */
    public static final int STANDBY_NODE = 1;

    /**
     * 节点类型：PRIMARY（主节点）
     */
    public static final String NODETYPE_MASTER = "PRIMARY";

    /**
     * 节点类型：STANDBY（备节点）,X表示数字
     */
    public static final String NODETYPE_STANDBY = "STANDBY";

    /**
     * 节点的流复制类型（主节点填 NONE，同步填 SYNC,异步填 ASYNC）
     */
    public static final String STREAMINGTYPE_NONE = "NONE";

    /**
     * 节点的流复制类型（主节点填 NONE，同步填 SYNC,异步填 ASYNC）
     */
    public static final String STREAMINGTYPE_SYNC = "SYNC";

    /**
     * 节点的流复制类型（主节点填 NONE，同步填 SYNC,异步填 ASYNC）
     */
    public static final String STREAMINGTYPE_ASYNC = "ASYNC";

    /**
     * 该节点没有VIP
     */
    public static final String VIPNUM_ZERO = "0";

    /**
     * 节点状态 t健康
     */
    public static final String NODE_HEALTHY = "t";

    /**
     * 节点状态f不健康
     */
    public static final String NODE_UNHEALTHY = "f";

    /**
     * agent监听rest api的端口
     */
    public static final String REST_API_AGENT_PORT = "7000";

    /**
     * rest api返回成功
     */
    public static final String REST_API_RESULT_SUCCESS = "success";

    /**
     * dbha  配置文件夹名
     */
    public static final String DBHA_CONF_DIR = "db_ha";

    /**
     * dbha  agent默认端口号
     */
    public static final int DBHA_AGENT_DEFAULT_PORT = 6666;


    /**
     * dbha  监控默认端口号
     */
    public static final int DBHA_MONITOR_DEFAULT_PORT = 8000;

    /**
     * dbha  主监控数量
     */
    public static final Integer PRIMARY_MONITOR_NUMBER = 1;

    public static final int IS_MONITORSERVER = 1;

    /**
     * 高可用主监控
     */
    public static final int PM_MONITOR_CLUSTER = 3;

    /**
     * dbha  默认路径
     */
    public static final String DEFAULT_DBHA_PATH = "/usr/local/db_ha";

}

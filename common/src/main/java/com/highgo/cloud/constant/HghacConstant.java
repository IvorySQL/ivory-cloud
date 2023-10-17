package com.highgo.cloud.constant;

public class HghacConstant {

    /**
     * 节点类型：PRIMARY（主节点）
     */
    public static final String NODETYPE_LEADER = "leader";

    /**
     * 节点类型：STANDBY（备节点）,X表示数字
     */
    public static final String NODETYPE_REPLICA = "replica";

    /**
     * 监听rest api的端口
     */
    public static final String REST_API_LISTEN_PORT = "8008";


    /**
     *  暂停集群
     */
    public static final String PAUSE_CLUSTER_CMD = "/usr/local/hghac/hac/hghactl/hghactl -c /usr/local/hghac/hac/hghac.yml pause --wait";

    /**
     *  恢复集群
     */
    public static final String RESUME_CLUSTER_CMD = "/usr/local/hghac/hac/hghactl/hghactl -c /usr/local/hghac/hac/hghac.yml resume --wait";

    /**
     *  启停hghac服务
     */
    public static final String STOP_HGHAC_SERVICE = "systemctl stop hghac";
    public static final String START_HGHAC_SERVICE = "systemctl start hghac";
    public static final String RESTART_HGHAC_SERVICE = "systemctl restart hghac";


}

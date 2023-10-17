package com.highgo.cloud.constant;

/**
 * @author lucunqiao
 * @date 2022/7/19
 */
public class ResourceManageConstant {

    /**
     * 扩容缩容  cpu
     */
    public static final String CPU_MANAGE = "cpu";

    /**
     * 扩容缩容  memory
     */
    public static final String MEMORY_MANAGE = "memory";

    /**
     * 扩容操作
     */
    public static final String EXPANSION = "e";

    /**
     * 缩容操作
     */
    public static final String SHRINK = "s";

    /**
     * 未占满磁盘空间
     */
    public static final int NOT_FULL_DISK = 0;

    /**
     * 占满磁盘空间
     */
    public static final int FULL_DISK = 1;


    /**
     * 扩容操作代码
     */
    public static final int EXPANSION_CODE = 1;

    /**
     * 缩容操作代码
     */
    public static final int SHRINK_CODE = 2;

    /**
     * systemd  memory默认单位
     */
    public static final String DEFAULT_MEMORY_UNIT = "G";

    /**
     * lvm  disk默认单位
     */
    public static final String DEFAULT_DISK_UNIT = "G";

    /**
     * tc  带宽默认单位
     */
    public static final String DEFAULT_BANDWIDTH_UNIT = "mbit";

    /**
     * cgroup中  net_cls  classid起始值
     */
    public static final String NET_CLS_CLASSID_START = "0x00010001";


    /**
     * 资源管理，创建db之前操作
     */
    public static final int RESOURCE_BEFORE_DB = 1;

    /**
     * 资源管理，创建db之后操作
     */
    public static final int RESOURCE_AFTER_DB = 2;

    /**
     * 服务器未初始化磁盘
     */
    public static final int UN_INIT_DISK = 0;

    /**
     * 服务器正在初始化磁盘
     */
    public static final int INIT_DISKING = 2;

    /**
     * 服务器已经初始化磁盘
     */
    public static final int HAS_INIT_DISK = 1;

    /**
     * 服务器初始化磁盘失败
     */
    public static final int INIT_DISK_FAILED = -1;

    /**
     * 不允许磁盘扩容
     */
    public static final int IMPOSSIBLE_SHRINK_DISK = 2;

}

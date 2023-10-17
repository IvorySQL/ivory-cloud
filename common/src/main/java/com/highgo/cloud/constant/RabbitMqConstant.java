package com.highgo.cloud.constant;

/**
 * mq主题-常量池
 * @author hg
 */
public class RabbitMqConstant {

	/**
	 * 虚拟机VM 单实例创建
	 */
    public final static String DB_DOUPATE = "db_doUpdate";
    /**
     * 虚拟机VM 单实例删除
     */
    public final static String DB_DELETE = "db_doDelete";
    /**
     * 虚拟机VM 高可用单实例删除
     */
    public final static String Dbha_DELETE ="dbha_doDelete";
    /**
     * 虚拟机VM 对等服务集群创建
     */
    public final static String DB_PEERSERVICE="db_peerService";
    /**
     * 裸金属-对等服务单实例更新
     */
    public final static String DB_DOUPDATEBMSPEERSERVICESINGLE="db_doUpdateBmsPeerServiceSingle";

    /**
     * 裸金属-dbha高可用多实例更新
     */
    public final static String DB_DOUPDATEBMSDBHAMULTI = "db_doUpdateBmsDbhaMulti";
    /**
     * 更新docker容器信息
     */
    public final static String DB_DOUPDATECNTRINFO = "db_doUpdateCNTRInfo";

}

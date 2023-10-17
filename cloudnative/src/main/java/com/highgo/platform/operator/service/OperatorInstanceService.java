package com.highgo.platform.operator.service;


import com.highgo.platform.operator.cr.DatabaseCluster;
import com.highgo.platform.operator.cr.bean.instance.Instance;

public interface OperatorInstanceService {

    /**
     * 构建cr instance节点
     * @param name 实例名称
     * @param replicas 实例副本数
     * @param cpu cpu配置
     * @param memory 内存配置
     * @param storage 磁盘配置
     * @param storageClass 存储类型
     * @return
     */
    public Instance geInstance(String name, int replicas, int cpu, int memory, String storage, String storageClass);

    /**
     * 获取cr instance 节点数量
     * @param databaseCluster
     * @return
     */
    public Integer getNodeNum(DatabaseCluster databaseCluster);

    /**
     * 获取cr instance ready节点数量
     * @param databaseCluster
     * @return
     */
    public Integer getNodeReadyNum(DatabaseCluster databaseCluster);

    boolean isAllPodRebuild(String clusterId, String namespace, String crName, String instanceId);
}

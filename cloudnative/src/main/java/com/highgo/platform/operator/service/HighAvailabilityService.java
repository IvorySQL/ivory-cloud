package com.highgo.platform.operator.service;

public interface HighAvailabilityService {

    /**
     * 在master节点启动指定clusterid的watcher
     * @param clusterId
     * @return
     */
    public boolean  startWatcherByClusterIdOnMaster(String clusterId);

    /**
     * 获取主节点的ip
     * @return
     */
    public String getMasterIp();
}

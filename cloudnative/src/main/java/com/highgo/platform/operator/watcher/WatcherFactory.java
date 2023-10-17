package com.highgo.platform.operator.watcher;

public interface WatcherFactory {

    /**
     * 在所有集群上启动watcher
     */
    public void initStart();

    public void refresh();

    /**
     * 在指定集群上启动watcher
     * @param clusterId k8s集群id
     * @return boolean
     */
    public  boolean startWatcherById(String clusterId);

    public boolean stopWatcherById(String clusterId);

    public void stopAllWatcher();


}

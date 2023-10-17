package com.highgo.platform.operator.watcher;

import com.highgo.platform.apiserver.service.K8sClusterService;
import com.highgo.platform.operator.ElectLeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Profile(value = "prod")
public class WatcherTask {
    private static final Logger logger = LoggerFactory.getLogger(WatcherTask.class);

    @Resource
    private ElectLeader electLeader;

    @Resource
    private K8sClusterService k8sClusterService;

    @Resource
    private WatcherFactory watcherFactory;

    @Scheduled(fixedDelayString = "${common.refreshWatcherTaskTime:600000}")
    public void refreshWatcher()  {
        if(!electLeader.isLeader){
            logger.info("[WatcherTask.refreWatcher] I am slaver, will not refresh watcher.");
        }else {
            watcherFactory.refresh();
            logger.info("[WatcherTask.refreWatcher] I am master, refresh watcher done.");
        }



    }

}

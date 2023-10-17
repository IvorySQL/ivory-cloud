package com.highgo.platform.apiserver.controller;

import com.highgo.platform.apiserver.model.vo.request.ClusterVO;
import com.highgo.platform.apiserver.model.vo.response.ActionResponse;
import com.highgo.platform.operator.watcher.WatcherFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/${common.request-path-prefix}/${common.version}")
public class WatcherController {

    @Autowired
    private WatcherFactory watcherFactory;

    @RequestMapping(value = "/watcher/action/start", method = {RequestMethod.POST})
    public ActionResponse startWatcher(@Validated @RequestBody ClusterVO clusterVO) {
        boolean result = watcherFactory.startWatcherById(clusterVO.getClusterId());
        if(result){
            return ActionResponse.actionSuccess();
        }else {
            return ActionResponse.actionFailed();
        }
    }

    @RequestMapping(value = "/watcher/action/stop", method = {RequestMethod.POST})
    public ActionResponse stopWatcher(@Validated @RequestBody ClusterVO clusterVO) {
        boolean result = watcherFactory.stopWatcherById(clusterVO.getClusterId());
        if(result){
            return ActionResponse.actionSuccess();
        }else {
            return ActionResponse.actionFailed();
        }
    }
}

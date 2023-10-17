package com.highgo.platform.apiserver.service;

import com.highgo.cloud.auth.model.dto.MonitorUserDto;
import com.highgo.cloud.auth.model.vo.UserVO;
import com.highgo.platform.apiserver.model.vo.request.CreateMonitorVO;
import com.highgo.platform.apiserver.model.vo.response.ActionResponse;

/**
 * @author lucunqiao
 * @date 2023/2/14
 */
public interface MonitorService {
    /**
     * description: 获取监控信息
     * date: 2023/2/16 11:05
     * @param createMonitorVO
     * @return: UserVO
     * @author: highgo-lucunqiao
     * @since JDK 1.8
     */
    UserVO monitor(CreateMonitorVO createMonitorVO);

    /**
     * description: 删除监控
     * date: 2023/3/3 10:17
     * @param userId
     * @param cluserId
     * @return: ActionResponse
     * @author: highgo-lucunqiao
     * @since JDK 1.8
     */
    ActionResponse delMonitor(int userId, String cluserId);

    /**
     * description: grafana创建回调
     * date: 2023/4/20 10:34
     * @param userDTO
     * @param b
     * @return: void
     * @author: highgo-lucunqiao
     * @since JDK 1.8
     */
    void createGrafanaCallback(MonitorUserDto userDTO, boolean b);

    /**
     * description: prometheus创建回调
     * date: 2023/4/20 10:34
     * @param userDTO
     * @param b
     * @return: void
     * @author: highgo-lucunqiao
     * @since JDK 1.8
     */
    void createPrometheusCallback(MonitorUserDto userDTO, boolean b);

    /**
     * description: 监控创建回调
     * date: 2023/4/20 10:35
     * @param userDTO
     * @param serverUrl
     * @param nodePort
     * @param status
     * @return: void
     * @author: highgo-lucunqiao
     * @since JDK 1.8
     */
    void createMonitorCallback(MonitorUserDto userDTO, String serverUrl, Integer nodePort, String status);
}

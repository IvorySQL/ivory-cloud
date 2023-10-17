package com.highgo.platform.apiserver.service.impl;

import com.highgo.cloud.auth.entity.User;
import com.highgo.cloud.auth.model.dto.MonitorUserDto;
import com.highgo.cloud.auth.model.vo.UserVO;
import com.highgo.cloud.auth.repository.AccountRepository;
import com.highgo.cloud.enums.MonitorStatus;
import com.highgo.cloud.model.ServerConnectVO;
import com.highgo.cloud.util.BeanUtil;
import com.highgo.cloud.util.SshUtil;
import com.highgo.platform.apiserver.model.po.K8sClusterInfoPO;
import com.highgo.platform.apiserver.model.vo.request.CreateMonitorVO;
import com.highgo.platform.apiserver.model.vo.response.ActionResponse;
import com.highgo.platform.apiserver.repository.K8sClusterInfoRepository;
import com.highgo.platform.apiserver.service.MonitorService;
import com.highgo.platform.errorcode.ClusterError;
import com.highgo.platform.exception.ClusterException;
import com.highgo.platform.utils.AsyncTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author lucunqiao
 * @date 2023/2/14
 */
@Service
public class MonitorServiceImpl implements MonitorService {
    private static final Logger logger = LoggerFactory.getLogger(MonitorServiceImpl.class);

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private K8sClusterInfoRepository k8sClusterInfoRepository;

    @Resource(name = "asyncTask")
    private AsyncTask asyncTask;

    @Value("${common.serviceName}")
    private String databaseName;

    /**
     * description: 获取用户监控信息
     * date: 2023/2/17 17:02
     *
     * @param createMonitorVO
     * @return: UserVO
     * @author: highgo-lucunqiao
     * @since JDK 1.8
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO monitor(CreateMonitorVO createMonitorVO) {
        MonitorUserDto userDto = new MonitorUserDto();
        BeanUtil.copyNotNullProperties(createMonitorVO, userDto);
        userDto.setId(createMonitorVO.getUserId());
        List<User> users = accountRepository.listByUserIdAndClusterId(userDto.getId(), userDto.getClusterId());
        UserVO userVO = new UserVO();
        if (!CollectionUtils.isEmpty(users)) {
            //用户监控信息存在， 直接返回
            BeanUtil.copyNotNullProperties(users.get(0), userVO);
            return userVO;
        }

        //监控信息不存在，需要新建
        userDto.setNamespace(userDto.getName() + userDto.getId());
        userDto.setMonitorStatus(MonitorStatus.CREATING.name());
        User user = accountRepository.getOne(userDto.getId());
        //修改数据入库
        user.setMonitorStatus(userDto.getMonitorStatus());
        user.setPrometheusReady(false);
        user.setAccessMode(userDto.getAccessMode());
        user.setNamespace(userDto.getNamespace());
        user.setClusterId(userDto.getClusterId());
        user.setCreatedTime(new Timestamp((new Date()).getTime()));
        user.setPrometheusReady(false);

        //查询k8s 集群信息
        Optional<K8sClusterInfoPO> K8sClusterInfoPO = k8sClusterInfoRepository.findByClusterId(userDto.getClusterId());
        if (!K8sClusterInfoPO.isPresent()) {
            logger.error("[MonitorServiceImpl.monitor] cluster is not exits. clusterId is {}", userDto.getClusterId());
            throw new ClusterException(ClusterError.CLUSTER_NOT_EXIST_ERROR);
        }
        //新建monitor
        asyncTask.createMonitor(userDto);
        BeanUtil.copyNotNullProperties(user, userVO);
        return userVO;
    }

    @Override
    public ActionResponse delMonitor(int userId, String cluserId) {
        try {
            //查询k8s 集群信息
            Optional<K8sClusterInfoPO> K8sClusterInfoPO = k8sClusterInfoRepository.findByClusterId(cluserId);
            if (!K8sClusterInfoPO.isPresent()) {
                logger.error("[MonitorServiceImpl.delMonitor] cluster is not exits. clusterId is {}", cluserId);
                throw new ClusterException(ClusterError.CLUSTER_NOT_EXIST_ERROR);
            }

            List<User> users = accountRepository.listByUserIdAndClusterId(userId, cluserId);
            if(CollectionUtils.isEmpty(users)){
                logger.error("[MonitorServiceImpl.delMonitor] monitor is not exits. clusterId is {}", cluserId);
                throw new ClusterException(ClusterError.CLUSTER_NOT_EXIST_ERROR);
            }

            //构建ssh连接master信息
            K8sClusterInfoPO k = K8sClusterInfoPO.get();
            ServerConnectVO server = ServerConnectVO
                    .builder()
                    .host(k.getServerUrl())
                    .user(k.getServerUser())
                    .password(k.getServerPass())
                    .port(k.getServerSshport())
                    .build();

            String userDir = "/opt/monitor/" + users.get(0).getNamespace();
            server.setCommand("kubectl delete -k " + userDir + "/" + databaseName);
            String result = SshUtil.remoteExeCommand(server);
            logger.info(result);
        } catch (Exception e) {
            throw new RuntimeException("Monitor deletion failure",e);
        }

        return ActionResponse.actionSuccess();
    }

    /**
     * description: 安装grafana回调
     * date: 2023/2/22 9:27
     * @param userDto
     * @param ready
     * @return: void
     * @author: highgo-lucunqiao
     * @since JDK 1.8
     */
    @Override
    public void createGrafanaCallback(MonitorUserDto userDto, boolean ready) {
        List<User> users = accountRepository.listByUserIdAndClusterId(userDto.getId(), userDto.getClusterId());
        if (!CollectionUtils.isEmpty(users)) {
            User user = users.get(0);
            user.setGrafanaReady(ready);
            accountRepository.save(user);
        }
    }

    /**
     * description: 安装监控回调
     * date: 2023/2/17 17:01
     *
     * @param userDto
     * @param hostIP
     * @param nodePort
     * @return: void
     * @author: highgo-lucunqiao
     * @since JDK 1.8
     */
    @Override
    public void createMonitorCallback(MonitorUserDto userDto, String hostIP, Integer nodePort, String status) {
        List<User> users = accountRepository.listByUserIdAndClusterId(userDto.getId(), userDto.getClusterId());
        if (!CollectionUtils.isEmpty(users)) {
            User user = users.get(0);
            user.setMonitorStatus(status);
            user.setMonitorUrl(hostIP + ":" + nodePort);
            accountRepository.save(user);
        }
    }

    /**
     * description: 安装prometheus是否完成
     * date: 2023/2/21 20:49
     *
     * @param userDto
     * @param ready
     * @return: void
     * @author: highgo-lucunqiao
     * @since JDK 1.8
     */
    @Override
    public void createPrometheusCallback(MonitorUserDto userDto, boolean ready) {
        List<User> users = accountRepository.listByUserIdAndClusterId(userDto.getId(), userDto.getClusterId());
        if (!CollectionUtils.isEmpty(users)) {
            User user = users.get(0);
            user.setPrometheusReady(ready);
            accountRepository.save(user);
        }
    }

}

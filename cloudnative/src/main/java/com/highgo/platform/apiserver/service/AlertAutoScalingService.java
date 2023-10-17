package com.highgo.platform.apiserver.service;

import com.highgo.platform.apiserver.model.dto.AutoScalingSwitchDTO;
import com.highgo.platform.apiserver.model.vo.request.AlertMessage;
import com.highgo.platform.apiserver.model.vo.request.ModifyAlertRuleVO;
import com.highgo.platform.apiserver.model.vo.request.ModifySwitchVO;
import com.highgo.platform.apiserver.model.vo.response.ActionResponse;
import com.highgo.platform.apiserver.model.vo.response.AutoScalingAlertRuleVO;
import com.highgo.platform.apiserver.model.vo.response.AutoScalingSwitchVO;
import com.highgo.cloud.enums.AutoScalingStatus;

import java.util.List;

/**
 * @author: highgo-lucunqiao
 * @date: 2023/4/19 10:32
 * @Description: 自动弹性伸缩Service
 */
public interface AlertAutoScalingService {
    /**
     * description:自动弹性伸缩
     * date: 2023/4/20 10:36
     * @param alertMessage 告警信息
     * @return: void
     * @author: highgo-lucunqiao
     * @since JDK 1.8
     */
    void alertAutoScaling(AlertMessage alertMessage);

    /**
     * description: 自动弹性伸缩回调
     * date: 2023/4/20 10:34
     * @param autoScalingHistoryId
     * @param status
     * @param operatorLog
     * @return: void
     * @author: highgo-lucunqiao
     * @since JDK 1.8
     */
    void autoScalingCallBack(String autoScalingHistoryId, AutoScalingStatus status, String operatorLog);

    /**
     * description: operator中自动弹性伸缩回调
     * date: 2023/4/20 14:09
     * @param instanceId
     * @param status
     * @param operatorLog
     * @return: void
     * @author: highgo-lucunqiao
     * @since JDK 1.8
     */
    void autoScalingOperatorCallBack(String instanceId, AutoScalingStatus status, String operatorLog);

    /**
     * description: 自动弹性伸缩回调
     * date: 2023/4/20 10:34
     * @param autoScalingHistoryId
     * @param status
     * @return: void
     * @author: highgo-lucunqiao
     * @since JDK 1.8
     */
    void updateAutoScalingStatus(String autoScalingHistoryId, AutoScalingStatus status);

    /**
     * description: 更新自动弹性伸缩历史记录的资源值
     * date: 2023/4/20 11:12
     * @param autoScalingHistoryId
     * @param sourceValue
     * @param targetValue
     * @return: void
     * @author: highgo-lucunqiao
     * @since JDK 1.8
     */
    void updateResourceValue(String autoScalingHistoryId, String sourceValue, String targetValue);

    /**
     * description: 获取自动弹性伸缩开关信息
     * date: 2023/4/23 10:52
     * @param userId
     * @param clusterId
     * @return: AutoScalingSwitchVO
     * @author: highgo-lucunqiao
     * @since JDK 1.8
     */
    AutoScalingSwitchDTO getAutoScalingSwitchDTO(String userId, String clusterId);

    /**
     * description: 修改弹性伸缩开关
     * date: 2023/4/23 13:12
     * @param userId
     * @param clusterId
     * @param modifySwitchVO
     * @return: ActionResponse
     * @author: highgo-lucunqiao
     * @since JDK 1.8
     */
    ActionResponse autoscalingSwitch(String userId, String clusterId, ModifySwitchVO modifySwitchVO);

    /**
     * description: 获取弹性伸缩开关
     * date: 2023/4/23 13:12
     * @param userId
     * @param clusterId
     * @return: AutoScalingSwitchVO
     * @author: highgo-lucunqiao
     * @since JDK 1.8
     */
    AutoScalingSwitchVO getAutoScalingSwitch(String userId, String clusterId);

    /**
     * description: 查询获取告警规则List
     * date: 2023/4/23 17:30
     * @param userId
     * @param clusterId
     * @return: List<AutoScalingAlertRuleVO>
     * @author: highgo-lucunqiao
     * @since JDK 1.8
     */
    List<AutoScalingAlertRuleVO> getAutoScalingAlertRule(String userId, String clusterId);

    /**
     * description: 初始化alert告警规则
     * date: 2023/4/23 17:33
     * @param userId
     * @param clusterId
     * @return: void
     * @author: highgo-lucunqiao
     * @since JDK 1.8
     */
    void initAutoScalingAlertRule(String userId, String clusterId);

    /**
     * description: 修改自动弹性伸缩的告警规则
     * date: 2023/4/24 19:11
     * @param modifyAlertRuleVOs
     * @return: List<AutoScalingAlertRuleVO>
     * @author: highgo-lucunqiao
     * @since JDK 1.8
     */
    List<AutoScalingAlertRuleVO> AutoScalingAlertRule(List<ModifyAlertRuleVO> modifyAlertRuleVOs);

    /**
     * description: 创建自动弹性伸缩的开关  用户第一次在某个集群下创建监控时调用
     * date: 2023/4/25 9:37
     * @param userId
     * @param clusterId
     * @return: void
     * @author: highgo-lucunqiao
     * @since JDK 1.8
     */
    void initAutoScalingSwitch(String userId, String clusterId);
}

package com.highgo.cloud.enums;

/**
 * @author: highgo-lucunqiao
 * @date: 2023/4/20 10:08
 * @Description: 自动弹性伸缩状态
 */
public enum AutoScalingStatus {

    //不处理
    NOTPROCESS
    //准备处理
    ,PREPAREPROCESS
    //处理中
    ,PROCESSING
    //处理成功
    ,SUCCESS
    //处理失败
    ,FAILED
}

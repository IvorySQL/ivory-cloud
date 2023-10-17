/* ------------------------------------------------
 *
 * 文件名称: CommonResult.java
 *
 * 摘要：
 *      此文件包含comment/reply。
 *
 * 作者信息及编写日期：jianan@highgo.com，20220414.
 *
 * 修改信息：
 * 2022.04.14，贾楠添加comment/reply.
 *
 * 版权信息：
 * Copyright (c) 2009-2019, HighGo Software Co.,Ltd. All rights reserved.
 *
 *文件路径：
 *		src/main/java/com/highgo/cloud/beans/CommonResult.java
 *
 *-------------------------------------------------
 */
package com.highgo.cloud.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommonResult {
    /**
     * 状态: 0 成功， 1 失败
      */
    private int code;
    /**
     * 信息
     */
    private String message;

    private boolean result;

}

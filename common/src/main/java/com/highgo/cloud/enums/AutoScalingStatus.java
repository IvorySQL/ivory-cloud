/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.highgo.cloud.enums;

/**
 * @author: highgo-lucunqiao
 * @date: 2023/4/20 10:08
 * @Description: 自动弹性伸缩状态
 */
public enum AutoScalingStatus {

    // 不处理
    NOTPROCESS
    // 准备处理
    , PREPAREPROCESS
    // 处理中
    , PROCESSING
    // 处理成功
    , SUCCESS
    // 处理失败
    , FAILED
}

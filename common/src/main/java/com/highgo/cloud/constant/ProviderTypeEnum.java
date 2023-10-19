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

package com.highgo.cloud.constant;

/**
 * 与table provider 内容一致
 * @author chushaolin
 *
 */
public enum ProviderTypeEnum {

    PHYSICAL(OrderContant.PROVIDER_CODE_PHYSICAL, OrderContant.PROVIDER_PURE_BMS_SERVER),
    HUAWEI(OrderContant.PROVIDER_CODE_HUAWEI, OrderContant.PROVIDER_HUAWEI),
    ALIYUN(OrderContant.PROVIDER_CODE_ALIYUN, OrderContant.PROVIDER_ALIYUN),
    TIANYI(OrderContant.PROVIDER_CODE_TIANYI, OrderContant.PROVIDER_TIANYI),
    LANGCHAO(OrderContant.PROVIDER_CODE_LANGCHAO, OrderContant.PROVIDER_LANGCHAO),
    XINFU(OrderContant.PROVIDER_CODE_XINFU, OrderContant.PROVIDER_XINFU);

    // 成员变量
    private String name;
    private int index;
    // 构造方法
    private ProviderTypeEnum(String name, int index) {
        this.name = name;
        this.index = index;
    }

    // 普通方法
    public static String getName(int index) {
        for (ProviderTypeEnum c : ProviderTypeEnum.values()) {
            if (c.getIndex() == index) {
                return c.name;
            }
        }
        return null;
    }
    // get set 方法
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }

}

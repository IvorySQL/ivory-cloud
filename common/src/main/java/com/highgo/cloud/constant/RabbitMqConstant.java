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
 * mq主题-常量池
 * @author hg
 */
public class RabbitMqConstant {

    /**
     * 虚拟机VM 单实例创建
     */
    public final static String DB_DOUPATE = "db_doUpdate";
    /**
     * 虚拟机VM 单实例删除
     */
    public final static String DB_DELETE = "db_doDelete";
    /**
     * 虚拟机VM 高可用单实例删除
     */
    public final static String Dbha_DELETE = "dbha_doDelete";
    /**
     * 虚拟机VM 对等服务集群创建
     */
    public final static String DB_PEERSERVICE = "db_peerService";
    /**
     * 裸金属-对等服务单实例更新
     */
    public final static String DB_DOUPDATEBMSPEERSERVICESINGLE = "db_doUpdateBmsPeerServiceSingle";

    /**
     * 裸金属-dbha高可用多实例更新
     */
    public final static String DB_DOUPDATEBMSDBHAMULTI = "db_doUpdateBmsDbhaMulti";
    /**
     * 更新docker容器信息
     */
    public final static String DB_DOUPDATECNTRINFO = "db_doUpdateCNTRInfo";

}

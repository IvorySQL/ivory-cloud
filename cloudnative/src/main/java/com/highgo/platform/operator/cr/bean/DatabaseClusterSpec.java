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

package com.highgo.platform.operator.cr.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.highgo.platform.operator.cr.bean.backup.Backup;
import com.highgo.platform.operator.cr.bean.backup.RestoreDatasource;
import com.highgo.platform.operator.cr.bean.service.DatabaseService;
import com.highgo.platform.operator.cr.bean.imagePullsecret.ImagePullSecret;
import com.highgo.platform.operator.cr.bean.instance.Instance;
import com.highgo.platform.operator.cr.bean.monitor.Monitor;
import com.highgo.platform.operator.cr.bean.patroni.Patroni;
import com.highgo.platform.operator.cr.bean.pgadmin.UserInterface;
import com.highgo.platform.operator.cr.bean.user.User;
import lombok.*;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DatabaseClusterSpec {

    /**
     * postgres的镜像
     */
    private String image;
    /**
     * postgres版本
     */
    private Integer postgresVersion;
    /**
     * 数据库端口号
     */
    private Integer port;
    /**
     * 镜像拉取策略
     */
    private String imagePullPolicy;
    /**
     * 实例信息
     */
    private List<Instance> instances;
    /**
     * 备份信息
     */
    private Backup backups;

    private List<ImagePullSecret> imagePullSecrets;

    private DatabaseService service;
    /**
     * 用户及数据库
     */
    private List<User> users;
    /**
     * pgadmin
     */
    private UserInterface userInterface;
    /**
     * 监控exporter
     */
    private Monitor monitoring;
    /**
     * 数据库param配置
     */
    private Patroni patroni;
    /**
     * 恢复的数据源
     */
    private RestoreDatasource dataSource;

}

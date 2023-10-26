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

package com.highgo.cloud.auth.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "users")
@Setter
@Getter
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "db_config_status", length = 20)
    private String dbConfigStatus;

    @Column(name = "udb_config_url", length = 256)
    private String dbConfigUrl;

    @Column(name = "email", length = 50)
    private String email;

    @Column(name = "first_login")
    private Integer firstLogin;

    @Column(name = "deleted", insertable = false)
    @ColumnDefault("0")
    private Integer deleted;

    /**
     * 0  关闭配额限制   1  开启配额限制
     */
    @Column(name = "bms_quota_switch")
    @ColumnDefault("0")
    private Integer bmsQuotaSwitch;

    @Column(name = "monitor_status", length = 20)
    @ColumnDefault("'uninstall'")
    private String monitorStatus;

    @Column(name = "monitor_url", length = 256)
    private String monitorUrl;

    @Column(name = "name", length = 20)
    private String name;

    @Column(name = "password", length = 200)
    private String password;

    @Column(name = "reg_time")
    private Timestamp regTime;

    @Column(name = "u_tel", length = 20)
    private String tel;
    /**
     * 命名空间
     */
    @Column(name = "namespace")
    private String namespace;

    /**
     * accessMode
     */
    @Column(name = "access_mode")
    private String accessMode;
    /**
     * cluster id
     */
    @Column(name = "cluster_id")
    private String clusterId;
    /**
     * prometheus is ready
     */
    @Column(name = "prometheus_ready")
    private Boolean prometheusReady;
    /**
     * grafana is ready
     */
    @Column(name = "grafana_ready")
    private Boolean grafanaReady;

    // @Column(name="type", length=20)
    // private Integer type;
    // bi-directional many-to-one association to UsersType
    // @ManyToOne(fetch = FetchType.LAZY)
    /**
     * 用户类型：体验用户、正常用户
     */
    @Column(name = "type")
    private int type;

    /**
     * 用户角色：routine or admin
     */
    @Column(name = "role")
    private int role;

    @Column(name = "created_time")
    private Timestamp createdTime;

    @Column(name = "updated_time")
    private Timestamp updatedTime;

    @Column(name = "deleted_time")
    private Timestamp deleted_time;

}
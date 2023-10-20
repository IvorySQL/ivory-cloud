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
 * 监控常量
 * @author chushaolin
 *
 */
public class MonitorConstant {

    /**
     * 数据库未被监控
     */
    public static final int NOT_MONITOR = 0;

    /**
     * 数据库已经被监控
     */
    public static final int IS_MONITORED = 1;

    /**
     * 数据库 监控失败
     */
    public static final int MONITORED_FAILED = -1;

    /**
     * db exporter 类型
     */
    public static final int PG_EXPORTER = 1;
    public static final int HIGHGO_EXPORTER = 2;

    /**
     * exporter port
     */
    public static final Integer PG_EXPORTER_PORT = 9187;

    // Grafana 用户相关常量
    // grafana用户名
    public static final String GRAFANA_USER = "user";
    // grafana登录名 一般和user一致
    public static final String GRAFANA_LOGIN = "login";
    // grafana用户邮箱
    public static final String GRAFANA_EMAIL = "email";
    // grafana用户登录密码
    public static final String GRAFANA_PASSWORD = "password";
    // grafana用户角色
    public static final String GRAFANA_ROLE = "role";
    // Grafana 不同的角色
    public static final String GRAFANA_ROLE_VIEWER = "Viewer";
    public static final String GRAFANA_ROLE_EDITOR = "Editor";
    public static final String GRAFANA_ROLE_ADMIN = "Admin";

    public static final String GRAFANA_NAME = "name";

    public static final String LOGIN = "/login";

    public static final String ADMIN_USER = "/api/admin/users";
    public static final String DATA_SOURCE = "/api/datasources";

    public static final String DELETE_DASHBOARDS = "/api/dashboards/uid/";
    public static final String DELETE_DATASOURCE = "/api/datasources/name/";

    public static final String DATA_SOURCE_NAME = "prometheus";

    public static final String DATA_SOURCE_ACESS = "proxy";

    public static final String CREATE_APIKEY = "/api/auth/keys";

    public static final String UPDATE_DASHBOARD = "/api/dashboards/db";
    public static final String DASHBOARDS_UID = "/api/dashboards/uid/";
    public static final String PERMISSION = "/permissions";
    public static final String PASSWORD = "/password";
    public static final String CLUSTER_JSON = "/dashboard/cluster_overview_9.4.json";
    public static final String SINGLE_JSON = "/dashboard/postgres_overview.json";

    /**
     * exporter默认端口
     */
    public static final Integer EXPORTER_PORT = 9187;

    /**
     * exporter默认端口
     */
    public static final Integer PATRONI_EXPORTER_PORT = 9547;
    public enum MonitorResultCode {

        ACTIVE("active", 0), FAILED("failed", 1), BUILDING("building", 2), UNINSTALL("uninstall", 3), ERROR("error",
                -1);
        private String name;
        private int code;
        MonitorResultCode(String name, int code) {
            this.name = name;
            this.code = code;
        }
        public static String getName(int code) {
            for (MonitorResultCode m : MonitorResultCode.values()) {
                if (m.getCode() == code) {
                    return m.name;
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
        public int getCode() {
            return code;
        }
        public void setCode(int code) {
            this.code = code;
        }

    }
}

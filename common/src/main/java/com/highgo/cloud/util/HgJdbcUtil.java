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

package com.highgo.cloud.util;

import com.highgo.cloud.exception.HgJdbcException;
import com.highgo.cloud.model.DatabaseConnectInfo;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: highgo-lucunqiao
 * @date: 2023/7/14 13:19
 * @Description: 瀚高jdbc util
 */
@Slf4j
public class HgJdbcUtil {

    /***
     * description: 连接数据库，执行查询sql
     * date: 2023/7/14 13:45
     * @param db
     * @param sql
     * @return: ResultSet
     * @author: highgo-lucunqiao
     * @since JDK 1.8
     */
    public static List<Map> executeQuery(DatabaseConnectInfo db, String sql) {
        try (Connection connection = getConnection(db);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)) {

            List<Map> list = new ArrayList<>();
            ResultSetMetaData md = resultSet.getMetaData();// 获取键名
            int columnCount = md.getColumnCount();// 获取列的数量
            while (resultSet.next()) {
                Map<String, Object> rowData = new HashMap<>();// 声明Map
                for (int i = 1; i <= columnCount; i++) {
                    rowData.put(md.getColumnName(i), resultSet.getObject(i));// 获取键名及值
                }
                list.add(rowData);
            }
            return list;
        } catch (SQLException e) {
            throw new HgJdbcException(e);
        }
    }

    /**
     * description: 连接数据库，执行update
     * date: 2023/7/14 13:46
     * @param db
     * @param sql
     * @return: void
     * @author: highgo-lucunqiao
     * @since JDK 1.8
     */
    public static void executeUpdate(DatabaseConnectInfo db, String sql) {
        try (Connection connection = getConnection(db);
                Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new HgJdbcException(e);
        }
    }

    /**
     * description: 获取数据库连接
     * date: 2023/7/14 13:46
     * @param db
     * @return: Connection
     * @author: highgo-lucunqiao
     * @since JDK 1.8
     */
    public static Connection getConnection(DatabaseConnectInfo db) {
        Connection conn = null;
        String connectUrl = String.format("jdbc:highgo://%s:%d/%s", db.getHost(), db.getPort(), db.getDbName());

        try {
            Class.forName("com.highgo.jdbc.Driver");
            conn = DriverManager.getConnection(connectUrl, db.getUserName(), db.getPassword());
        } catch (Exception e) {
            throw new HgJdbcException(e);
        }
        return conn;
    }

}

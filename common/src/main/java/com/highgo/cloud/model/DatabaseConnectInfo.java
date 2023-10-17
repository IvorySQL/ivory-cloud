package com.highgo.cloud.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author: highgo-lucunqiao
 * @date: 2023/7/14 13:25
 * @Description: 连接db信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class DatabaseConnectInfo {
    @Builder.Default
    private String userName = "sysdba";
    private String password;
    @Builder.Default
    private Integer port = 5866;
    @Builder.Default
    private String dbName = "highgo";
    private String host;
}

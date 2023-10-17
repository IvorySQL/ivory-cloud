package com.highgo.cloud.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lucunqiao
 * @date 2023/2/15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServerConnectVO {
    private String host;
    @Builder.Default
    private int port = 22;
    private String user;
    private String password;
    private String command;
    private String scriptName;
}

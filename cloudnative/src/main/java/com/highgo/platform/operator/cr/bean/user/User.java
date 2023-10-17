package com.highgo.platform.operator.cr.bean.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    private String name;

    /**
     * 账户权限，多个权限用空格分开
     * LOAGIN CREATEDB CREATEROLE INHERIT REPLICATION BYPASSRLS
     */
    private String options;

    private List<String> databases;

}

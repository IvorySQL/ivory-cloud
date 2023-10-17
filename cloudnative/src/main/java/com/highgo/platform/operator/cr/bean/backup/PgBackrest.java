package com.highgo.platform.operator.cr.bean.backup;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class PgBackrest {

    /**
     *  备份功能容器镜像
     */
    private String image;
    /**
     *  手动备份
     */
    private Manual manual;
    /**
     *  备份信息列表
     */
    private List<Repo> repos;
    /**
     *  备份全局配置信息
     */
    private List<Object> configuration;
    /**
     *  全局配置
     */
    private Map<String, String> global;
    /**
     *  恢复
     */
    private Restore restore;
}

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

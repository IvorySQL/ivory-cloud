package com.highgo.platform.apiserver.model.po;

import com.highgo.cloud.enums.NetworkType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

/**
 * @author srk
 */
@Entity
@Table(name = "instance_network")
@ApiModel("Postgresql实例网络信息")
@Data
public class InstanceNetworkPO extends BaseEntity {

    private static final long serialVersionUID = -1665996121000L;

    private String instanceId;

    @ApiModelProperty(value = "读写信息(只读/读写)")
    @Enumerated(EnumType.STRING)
    private NetworkType type;

    private String nodeIp;

    private Integer nodePort;

    /**
     * 内网连接svc信息
     */
    private String service;

    /**
     * 内网连接端口
     */
    private Integer port;

}

package com.highgo.platform.apiserver.model.po;


import lombok.Data;

import javax.persistence.*;


/**
 * @author srk
 */
@Data
@Entity
@Table(name = "instance_event")
public class InstanceEventPO extends BaseEntity {

    private static final long serialVersionUID = -1667696070000L;

    /**
     * 实例ID
     */
    private String instanceId;
    /**
     * 实例副本数量
     */
    private Integer nodeNum;
    /**
     * 运行中副本数量
     */
    private Integer nodeReadyNum;
    /**
     * statefulset 事件
     */
    private String stsevent;
    /**
     * pod 事件
     */
    private String podevent;
    /**
     * cr resource version
     */
    private Long resourceVersion;
}

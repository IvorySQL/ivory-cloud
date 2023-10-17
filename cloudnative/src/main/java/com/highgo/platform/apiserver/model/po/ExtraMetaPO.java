package com.highgo.platform.apiserver.model.po;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * @author srk
 */
@Entity
@Table(name = "extra_meta")
@Data
public class ExtraMetaPO extends BaseEntity {
    private static final long serialVersionUID = 4619332251971599408L;

    private String instanceId;
    private String name;
    private String value;
}


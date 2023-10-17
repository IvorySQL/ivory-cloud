package com.highgo.cloud.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author srk
 */

@AllArgsConstructor
@Getter
public enum IvoryVersion {

    /**
     * 原生postgres 15.3
     */
    IVORY23("2.3");


    private final String key;

}

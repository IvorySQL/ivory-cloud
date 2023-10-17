package com.highgo.cloud.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum HighgoDBVersion {

    /**
     * 原生postgres 14.4
     */
    HGDB458("4.5.8");


    private final String key;

}

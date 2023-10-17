package com.highgo.cloud.enums;

/**
 * @author: highgo-lucunqiao
 * @date: 2023/4/23 9:16
 * @Description: 自行弹性伸缩告警规则条件
 */
public enum AutoScalingConditionals {
    GT(">"),
    GE(">="),
    LT("<"),
    LE("<="),
    EQ("=");

    private String value;

    AutoScalingConditionals(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

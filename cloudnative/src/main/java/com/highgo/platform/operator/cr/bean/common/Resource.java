package com.highgo.platform.operator.cr.bean.common;

import lombok.Data;

/**
 * cpu memory 配置对象
 */

@Data
public class Resource {

    private Limit limits;

    private Request requests;
}

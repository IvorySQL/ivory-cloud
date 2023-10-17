package com.highgo.platform.apiserver.model.vo.request;

import lombok.Data;

/**
 * @author: highgo-lucunqiao
 * @date: 2023/4/23 17:39
 * @Description: 修改alert规则请求VO
 */
@Data
public class ModifyAlertRuleVO {

    private String id;

    private Integer threshold;

    private long duration;
}

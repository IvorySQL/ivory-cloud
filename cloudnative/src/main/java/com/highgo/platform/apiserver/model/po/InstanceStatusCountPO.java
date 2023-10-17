package com.highgo.platform.apiserver.model.po;

import com.highgo.cloud.enums.InstanceStatus;
import lombok.*;

/**
 *  映射数据库查询实例状态数量统计
 * @author srk
 */
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstanceStatusCountPO {

    /**
     * 实例状态
     */
    private InstanceStatus status;

    /**
     * 实例统计数量
     */
    private Integer num;


}

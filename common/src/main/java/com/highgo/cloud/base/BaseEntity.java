/* ------------------------------------------------
 *
 * 文件名称: BaseEntity.java
 *
 * 摘要：
 *      此文件包含通用字段， is_del 根据需求自行添加。
 *
 * 作者信息及编写日期：zourenli@highgo.com，20220627.
 *
 * 修改信息：
 * 2022.6.27，邹仁利，添加通用字段， is_del 根据需求自行添加.
 *
 * 版权信息：
 * Copyright (c) 2009-2019, HighGo Software Co.,Ltd. All rights reserved.
 *
 *文件路径：
 *		src/main/java/com/highgo/cloud/base/BaseEntity.java
 *
 *-------------------------------------------------
 */
package com.highgo.cloud.base;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 通用字段， 实体通用的创建，修改，删除时间
 * @author zou
 * @date 2022年6月27日15:48:53
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(CloudPlatformEntityListener.class)
public abstract class BaseEntity implements Serializable {

    private static final long serialVersionUID = -5981097980916945570L;

    /**
     * 创建时间
     */
    private Timestamp createdTime;
    /**
     * 更新时间
     */
    private Timestamp updatedTime;
    /**
     * 删除时间
     */
    private Timestamp deletedTime;
    /**
     * 是否已删除
     */
    private int deleted = 0;


}

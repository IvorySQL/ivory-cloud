/* ------------------------------------------------
 *
 * 文件名称: BaseMapper.java
 *
 * 摘要：
 *      此文件包含BaseMapper接口。
 *
 * 作者信息及编写日期：zourenli@highgo.com，20220627.
 *
 * 修改信息：
 * 2022.6.27，邹仁利，添加BaseMapper interface.
 *
 * 版权信息：
 * Copyright (c) 2009-2019, HighGo Software Co.,Ltd. All rights reserved.
 *
 *文件路径：
 *		src/main/java/com/highgo/cloud/base/BaseMapper.java
 *
 *-------------------------------------------------
 */
package com.highgo.cloud.base;

import java.util.List;

/**
 * @author zou
 * @date 2022年6月27日15:49:56
 */
public interface BaseMapper<D, E> {

    /**
     * DTO转Entity
     * @param dto /
     * @return /
     */
    E toEntity(D dto);

    /**
     * Entity转DTO
     * @param entity /
     * @return /
     */
    D toDto(E entity);

    /**
     * DTO集合转Entity集合
     * @param dtoList /
     * @return /
     */
    List <E> toEntity(List<D> dtoList);

    /**
     * Entity集合转DTO集合
     * @param entityList /
     * @return /
     */
    List <D> toDto(List<E> entityList);
}

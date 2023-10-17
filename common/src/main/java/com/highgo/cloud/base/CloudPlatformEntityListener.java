package com.highgo.cloud.base;

import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import java.sql.Timestamp;

/**
 * @author srk
 * @version 1.0
 * @project micro_service_cloud
 * @description 云平台实体操作监听器
 * @date 2023/7/26 11:13:15
 */
public class CloudPlatformEntityListener {
    /**
     * @description 保存实体前操作
     *
     * @param: entity
     * @return void
     * @author srk
     * @date 2023/7/26 13:09
     */
    @PrePersist
    public void prePersist(Object entity) {
        if (entity instanceof BaseEntity) {
            BaseEntity baseEntity = (BaseEntity) entity;
            baseEntity.setCreatedTime(new Timestamp(System.currentTimeMillis()));
            baseEntity.setUpdatedTime(null);
            baseEntity.setDeletedTime(null);
            baseEntity.setDeleted(0);
        }
    }

    /**
     * @description 更新实体前操作
     *
     * @param: entity
     * @return void
     * @author srk
     * @date 2023/7/26 13:08
     */
    @PreUpdate
    public void preUpdate(Object entity) {
        if (entity instanceof BaseEntity) {
            BaseEntity baseEntity = (BaseEntity) entity;
            if (baseEntity.getDeleted() == 1) {
                baseEntity.setDeletedTime(new Timestamp(System.currentTimeMillis()));
            } else {
                baseEntity.setUpdatedTime(new Timestamp(System.currentTimeMillis()));
            }
        }
    }

    /**
     * @description 删除实体前操作
     *
     * @param: entity
     * @return void
     * @author srk
     * @date 2023/7/26 13:08
     */
    @PreRemove
    public void preRemove(Object entity) {
        if (entity instanceof BaseEntity) {
            BaseEntity baseEntity = (BaseEntity) entity;
            baseEntity.setDeletedTime(new Timestamp(System.currentTimeMillis()));
            baseEntity.setDeleted(1);
        }
    }
}
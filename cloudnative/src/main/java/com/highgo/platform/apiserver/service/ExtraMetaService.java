package com.highgo.platform.apiserver.service;


import com.highgo.platform.apiserver.model.po.ExtraMetaPO;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 扩展表服务类
 */
public interface ExtraMetaService {

    /**
     * 保存实例的扩展属性
     *
     * @param instanceId
     * @param name
     * @param value
     */
    void saveExtraMeta(String instanceId, String name, String value);

    /**
     * 保存扩展属性

     */
    void saveExtraMeta(ExtraMetaPO extraMetaPO);

    /**
     * 保存实例的扩展属性
     *
     * @param instanceId
     * @param name
     * @param value
     * @param createdAt
     */
    void saveExtraMeta(String instanceId, String name, String value, Date createdAt);

    /**
     * 查询实例的扩展属性
     *
     * @param instanceId
     * @param name
     */
    Optional<ExtraMetaPO> findExtraMetaByInstanceIdAndName(String instanceId, String name);

    /**
     * 扩展信息入库 {key,value,k2:v2}
     * @param extraMetaMap
     */
    public void saveMany(String instanceId, Map<String, String> extraMetaMap);

    public void deleteByInstanceId(String instanceId);

    public void deleteByInstanceId(String instanceId, Date date);

    public List<ExtraMetaPO> findAllByInstanceId(String instanceId);

    public void updateValueByNameAndInstanceId(String instanceId, String name, String value, Date date);

    public void updateValueByNameAndInstanceId(String instanceId, String name, String value);

    public void deleteByInstanceIdAndName(String instanceId, String name);
}

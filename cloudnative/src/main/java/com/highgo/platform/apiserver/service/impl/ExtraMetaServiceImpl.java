/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.highgo.platform.apiserver.service.impl;

import com.highgo.platform.apiserver.model.po.ExtraMetaPO;
import com.highgo.platform.apiserver.repository.ExtraMetaRepository;
import com.highgo.platform.apiserver.service.ExtraMetaService;
import com.highgo.cloud.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ExtraMetaServiceImpl implements ExtraMetaService {

    @Autowired
    private ExtraMetaRepository extraMetaRepository;

    /**
     * 保存实例的扩展属性
     *
     * @param instanceId
     * @param name
     * @param value
     */
    @Override
    @Transactional
    public void saveExtraMeta(String instanceId, String name, String value) {
        saveExtraMeta(instanceId, name, value, CommonUtil.getUTCDate());
    }

    @Override
    public void saveExtraMeta(ExtraMetaPO extraMetaPO) {
        extraMetaRepository.save(extraMetaPO);
    }

    /**
     * 保存实例的扩展属性
     *
     * @param instanceId
     * @param name
     * @param value
     * @param createdAt
     */
    @Override
    public void saveExtraMeta(String instanceId, String name, String value, Date createdAt) {

        Optional<ExtraMetaPO> byInstanceIdAndName = extraMetaRepository.findByInstanceIdAndName(instanceId, name);
        if (byInstanceIdAndName.isPresent()) {
            // 已经存在改属性
            extraMetaRepository.updateValueByNameAndInstanceId(instanceId, name, value, createdAt);
        } else {
            // 属性不存在
            ExtraMetaPO extraMetaPO = new ExtraMetaPO();
            extraMetaPO.setCreatedAt(createdAt);
            extraMetaPO.setInstanceId(instanceId);
            extraMetaPO.setName(name);
            extraMetaPO.setValue(value);
            extraMetaRepository.save(extraMetaPO);
        }

    }

    /**
     * 查询实例的扩展属性
     *
     * @param instanceId
     * @param name
     */
    @Override
    public Optional<ExtraMetaPO> findExtraMetaByInstanceIdAndName(String instanceId, String name) {
        return extraMetaRepository.findByInstanceIdAndName(instanceId, name);
    }

    @Override
    public void saveMany(String instanceId, Map<String, String> extraMetaMap) {
        if (extraMetaMap == null) {
            return;
        }
        List<ExtraMetaPO> extraMetaPOList = new ArrayList<>();
        Date date = CommonUtil.getUTCDate();
        for (String key : extraMetaMap.keySet()) {
            ExtraMetaPO extraMetaPO = new ExtraMetaPO();
            extraMetaPO.setInstanceId(instanceId);
            extraMetaPO.setName(key);
            extraMetaPO.setValue(extraMetaMap.get(key));
            extraMetaPO.setCreatedAt(date);
            extraMetaPOList.add(extraMetaPO);
        }
        extraMetaRepository.saveAll(extraMetaPOList);
    }

    public void deleteByInstanceId(String instanceId) {
        Date date = CommonUtil.getUTCDate();
        extraMetaRepository.deleteByInstanceId(instanceId, date);
    }

    public void deleteByInstanceId(String instanceId, Date date) {
        extraMetaRepository.deleteByInstanceId(instanceId, date);
    }

    public List<ExtraMetaPO> findAllByInstanceId(String instanceId) {
        return extraMetaRepository.findByInstanceId(instanceId);
    }

    @Override
    public void updateValueByNameAndInstanceId(String instanceId, String name, String value, Date date) {
        extraMetaRepository.updateValueByNameAndInstanceId(instanceId, name, value, date);
    }

    @Override
    public void updateValueByNameAndInstanceId(String instanceId, String name, String value) {
        extraMetaRepository.updateValueByNameAndInstanceId(instanceId, name, value, CommonUtil.getUTCDate());
    }

    @Override
    public void deleteByInstanceIdAndName(String instanceId, String name) {
        extraMetaRepository.deleteByInstanceIdAndName(instanceId, name);
    }
}

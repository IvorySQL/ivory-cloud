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
        if(byInstanceIdAndName.isPresent()){
            //已经存在改属性
            extraMetaRepository.updateValueByNameAndInstanceId(instanceId, name, value, createdAt);
        }else{
            //属性不存在
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
    public void saveMany(String instanceId, Map<String, String> extraMetaMap){
        if(extraMetaMap==null){
            return;
        }
        List<ExtraMetaPO> extraMetaPOList = new ArrayList<>();
        Date date = CommonUtil.getUTCDate();
        for(String key: extraMetaMap.keySet()){
            ExtraMetaPO extraMetaPO = new ExtraMetaPO();
            extraMetaPO.setInstanceId(instanceId);
            extraMetaPO.setName(key);
            extraMetaPO.setValue(extraMetaMap.get(key));
            extraMetaPO.setCreatedAt(date);
            extraMetaPOList.add(extraMetaPO);
        }
        extraMetaRepository.saveAll(extraMetaPOList);
    }

    public void deleteByInstanceId(String instanceId){
        Date date = CommonUtil.getUTCDate();
        extraMetaRepository.deleteByInstanceId(instanceId, date);
    }

    public void deleteByInstanceId(String instanceId, Date date){
        extraMetaRepository.deleteByInstanceId(instanceId, date);
    }

    public List<ExtraMetaPO> findAllByInstanceId(String instanceId){
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
    public void deleteByInstanceIdAndName(String instanceId, String name){
        extraMetaRepository.deleteByInstanceIdAndName(instanceId, name);
    }
}

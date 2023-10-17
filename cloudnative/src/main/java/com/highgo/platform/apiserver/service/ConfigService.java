package com.highgo.platform.apiserver.service;

import com.highgo.cloud.model.PageInfo;
import com.highgo.platform.apiserver.model.vo.request.ModifyConfigChangeVO;
import com.highgo.platform.apiserver.model.vo.response.ActionResponse;
import com.highgo.platform.apiserver.model.vo.response.ConfigChangeHistoryVO;
import com.highgo.platform.apiserver.model.vo.response.ConfigChangeVO;
import com.highgo.platform.apiserver.model.vo.response.ConfigParamInfoVO;

import java.util.List;

public interface ConfigService {

    /**
     * 参数配置列表
     * @param id
     * @return
     */
    public List<ConfigParamInfoVO> listParamters(String id);

    /**
     * 修改参数配置
     * @param id
     * @param modifyConfigChangeParam
     * @return
     */
    public ActionResponse modifyParameters(String id, ModifyConfigChangeVO modifyConfigChangeParam);

    /**
     * 修改参数完成回调方法
     * @param id
     * @param configHistoryId
     */
    public void modifyParametersCallback(String id, String configHistoryId, boolean result);

    /**
     * 参数配置修改历史记录分页
     * @param pageNo
     * @param pageSize
     * @return
     */
    public PageInfo<List<ConfigChangeHistoryVO>> listHistory(String id, int pageNo, int pageSize);

    /**
     * 指定参数修改历史记录的参数变更列表
     * @param id
     * @param configChangeHistoryId
     * @return
     */
    public List<ConfigChangeVO> listConfigChangeByHistory(String id, String configChangeHistoryId);

}

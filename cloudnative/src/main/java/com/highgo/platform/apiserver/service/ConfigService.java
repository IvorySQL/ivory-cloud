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

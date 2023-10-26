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

import com.highgo.platform.apiserver.model.vo.request.RestoreInstanceVO;
import com.highgo.platform.apiserver.model.vo.response.ActionResponse;

public interface RestoreService {

    /**
     * 恢复到当前实例
     * @param id 实例id
     * @param restoreInstanceVO 恢复参数
     * @return
     */
    ActionResponse restoreInstance(String id, RestoreInstanceVO restoreInstanceVO);

    /**
     * 恢复完成回调方法
     * @param id 实例id
     * @param originalBackupId 备份id
     * @param result 恢复结果 成功-true 失败-false
     */
    void restoreInstanceCallBack(String id, String originalBackupId, boolean result);

}

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

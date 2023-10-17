package com.highgo.platform.apiserver.service;

import com.highgo.platform.apiserver.model.vo.request.DatabaseVO;
import com.highgo.platform.apiserver.model.vo.response.ActionResponse;

import java.util.List;

/**
 * @author lucunqiao
 * @date 2022/12/14
 */
public interface DBDatabaseService {


    /**
     * 创建数据库
     *
     * @param instanceId
     * @param databaseVO
     * @return
     */
    ActionResponse createDatabase(String instanceId, DatabaseVO databaseVO);

    /**
     * 删除数据库
     *
     * @param instanceId
     * @param dbname
     * @return
     */
    ActionResponse deleteDatabase(String instanceId, String dbname);

    /**
     * 数据库列表
     *
     * @param instanceId
     * @return
     */
    List<DatabaseVO> listDatabases(String instanceId);

}

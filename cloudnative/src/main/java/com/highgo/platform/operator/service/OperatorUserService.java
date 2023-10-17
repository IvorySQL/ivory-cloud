package com.highgo.platform.operator.service;


import com.highgo.platform.apiserver.model.vo.request.DatabaseVO;
import com.highgo.platform.apiserver.model.vo.request.DatabaseUserVO;

public interface OperatorUserService {

    void resetPassword(String clusterId, String namespace, String crName, String userName, String password);

    void createUser(String clusterId, String namespace, String inName, DatabaseUserVO databaseUserVO);

    void deleteDbUser(String clusterId, String namespace, String inName, String userName);

    void createDatabase(String clusterId, String namespace, String inName, DatabaseVO databaseVO);

}

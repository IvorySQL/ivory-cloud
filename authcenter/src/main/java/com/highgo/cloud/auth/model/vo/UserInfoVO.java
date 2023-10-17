package com.highgo.cloud.auth.model.vo;

import java.util.List;

/* ------------------------------------------------ 
 * 
 * 文件名称: UserInfoVO.java
 *
 * 摘要： 
 *      用户登录成功后，返回给前端的用户信息。
 *
 * 作者信息及编写日期：chushaolin@highgo.com，2023-9-27 16:13:32.
 *
 * 修改信息：（如果需要）
 * 2023-9-27，chushaolin@highgo.com，具体修改内容。
 * 
 * 版权信息：
 * Copyright (c) 2009-2099, HighGo Software Co.,Ltd. All rights reserved. 
 *
 *文件路径：
 *		com.highgo.cloud.auth.model.vo.UserInfoVO.java
 *
 *-------------------------------------------------
 */
public class UserInfoVO {
	/**
	 * 用户的ID
	 */
    int userId;
    
    /**
     * 用户的名字
     */
    String userName;
    /**
     * 角色
     */
    private List<String> roles;

    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public List<String> getRoles() {
        return roles;
    }
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

}


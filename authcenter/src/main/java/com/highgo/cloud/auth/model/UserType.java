package com.highgo.cloud.auth.model;

import com.highgo.cloud.auth.constant.UserConstants;

/* ------------------------------------------------ 
 * 
 * 文件名称: UserType.java
 *
 * 摘要： 
 *      请填写该类实现的功能。
 *
 * 作者信息及编写日期：chushaolin@highgo.com，2023-9-25 16:30:23.
 *
 * 修改信息：（如果需要）
 * 2023-9-25，chushaolin@highgo.com，具体修改内容。
 * 
 * 版权信息：
 * Copyright (c) 2009-2099, HighGo Software Co.,Ltd. All rights reserved. 
 *
 *文件路径：
 *		com.highgo.cloud.auther.model.UserType.java
 *
 *-------------------------------------------------
 */
/**
 * 与table users_type 内容一致
 * @author chushaolin
 *
 */
public enum UserType {

    TRIVAL(UserConstants.ROLE_ROUTINE_NAME, 1),
    ROUTINE(UserConstants.ROLE_ROUTINE_NAME, 2),
    ADMIN(UserConstants.ROLE_ADMIN_NAME, 3);

    // 成员变量
    private String name;
    private int index;
    // 构造方法
    private UserType(String name, int index) {
        this.name = name;
        this.index = index;
    }

 // 普通方法
    public static String getName(int index) {
        for (UserType c : UserType.values()) {
            if (c.getIndex() == index) {
                return c.name;
            }
        }
        return null;
    }
    // get set 方法
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }

}

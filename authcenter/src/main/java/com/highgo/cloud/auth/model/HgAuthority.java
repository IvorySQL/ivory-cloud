package com.highgo.cloud.auth.model;

import org.springframework.security.core.GrantedAuthority;

/* ------------------------------------------------ 
 * 
 * 文件名称: HgAuthority.java
 *
 * 摘要： 
 *      授权。
 *
 * 作者信息及编写日期：chushaolin@highgo.com，2023-9-27 9:21:04.
 *
 * 修改信息：（如果需要）
 * 2023-9-27，chushaolin@highgo.com，具体修改内容。
 * 
 * 版权信息：
 * Copyright (c) 2009-2099, HighGo Software Co.,Ltd. All rights reserved. 
 *
 *文件路径：
 *		com.highgo.cloud.auth.model.HgAuthority.java
 *
 *-------------------------------------------------
 */
public class HgAuthority implements GrantedAuthority{

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String authority;


    public void setAuthority(String authority) {
        this.authority = authority;
    }


    @Override
    public String getAuthority() {
        // TODO Auto-generated method stub
        return authority;
    }

}

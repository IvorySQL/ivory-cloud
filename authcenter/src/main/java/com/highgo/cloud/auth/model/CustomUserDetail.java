package com.highgo.cloud.auth.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;


/* ------------------------------------------------ 
 * 
 * 文件名称: CustomUserDetail.java
 *
 * 摘要： 
 *      定制用户信息。
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
 *		com.highgo.cloud.auth.model.CustomUserDetail.java
 *
 *-------------------------------------------------
 */
public class CustomUserDetail extends User  {
    public CustomUserDetail(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        // TODO Auto-generated constructor stub
    }

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private int userId;

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

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }


    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        if (roles == null || roles.isEmpty()) {
            return new ArrayList<>();
        }

        List<GrantedAuthority> authorities = new ArrayList<>(roles.size());
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }

        return authorities;
    }


}

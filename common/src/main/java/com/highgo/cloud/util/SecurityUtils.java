/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.highgo.cloud.util;

import com.highgo.cloud.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
@Slf4j
public class SecurityUtils {

    ///**
    // * 获取当前登录的用户
    // * @return UserDetails
    // */
    //public static UserDetails getCurrentUser() {
    //    UserDetailsService userDetailsService = SpringContextHolder.getBean(UserDetailsService.class);
    //    return userDetailsService.loadUserByUsername(getCurrentUser());
    //}

    /**
     * 获取用户名
     * @return 获取用户名
     */
    public static String getUserName() {
        UserDetails userDetails = getUserDetails();
        return userDetails.getUsername();
    }

    /**
     * 获取系统用户名称
     * @return 系统用户名称
     */
    public static UserDetails getUserDetails() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new BadRequestException(HttpStatus.UNAUTHORIZED, "当前登录状态过期");
        }
        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userDetails;
        }
        throw new BadRequestException(HttpStatus.UNAUTHORIZED, "找不到当前登录的信息");
    }

    ///**
    // * 获取系统用户ID
    // * @return 系统用户ID
    // */
    //public static Long getCurrentUserId() {
    //    UserDetails userDetails = getUserDetails();
    //    return new JSONObject(new JSONObject(userDetails).get("user")).get("id", Long.class);
    //}

    ///**
    // * 获取当前用户的数据权限
    // * @return /
    // */
    //public static List<Long> getCurrentUserDataScope(){
    //    UserDetails userDetails = getUserDetails();
    //    JSONArray array = JSONUtil.parseArray(new JSONObject(userDetails).get("dataScopes"));
    //    return JSONUtil.toList(array,Long.class);
    //}

    ///**
    // * 获取数据权限级别
    // * @return 级别
    // */
    //public static String getDataScopeType() {
    //    List<Long> dataScopes = getCurrentUserDataScope();
    //    if(dataScopes.size() != 0){
    //        return "";
    //    }
    //    return DataScopeEnum.ALL.getValue();
    //}
}

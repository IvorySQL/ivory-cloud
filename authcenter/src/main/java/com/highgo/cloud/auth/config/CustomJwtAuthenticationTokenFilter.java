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

package com.highgo.cloud.auth.config;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.highgo.cloud.auth.constant.SecurityConstants;
import com.highgo.cloud.auth.model.CustomUserDetail;
import com.highgo.cloud.auth.service.impl.UserServiceImpl;
import com.highgo.cloud.auth.util.JwtTokenUtil;
import com.highgo.cloud.util.CommonUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 认证token filter
 * 
 * @author chushaolin
 *
 */
@Component
@Slf4j
public class CustomJwtAuthenticationTokenFilter extends OncePerRequestFilter {

    // @Resource(name = "authServiceImpl")
    // AuthServiceImpl userServiceImpl;

    @Resource(name = "userServiceImpl")
    UserServiceImpl userServiceImpl;

    @Value("${cors.access.control.maxAge:7200}")
    String corsAccessControlMaxAge;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // String methodString = request.getMethod();
        // log.debug("Requeset uri: [{}]", requestUri);

        // allow regin
        response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, request.getHeader("origin"));

        // allow credential
        response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");

        // allow method
        response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT, DELETE, OPTIONS, HEAD");

        // allow header
        response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,
                "Access-Control-Allow-Headers, Access-Control-Allow-Methods, Access-Control-Allow-Origin, Content-Type, x-xsrf-token, authorization");
        // allow max age
        response.addHeader(HttpHeaders.ACCESS_CONTROL_MAX_AGE, corsAccessControlMaxAge);

        // // 暴露哪些头部信息（因为跨域访问默认不能获取全部头部信息）给前端VUE
        // // 目前response的token字段允许前端获取，以便后续发送的时候携带该字段进行鉴权
        response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, SecurityConstants.HEADER);

        // TODO:如再遇到前端报The value of the 'Access-Control-Allow-Origin' header in the
        // response must not be the wildcard '*' when the request's credentials mode is
        // 'include'. The credentials mode of requests initiated by t
        // 则删除该行，并将前端的axios.defaults.withCredentials = true取消
        response.setHeader("Access-Control-Allow-Credentials", "true");

        // // 配置的为允许前端vue发过来的消息头
        // response.setHeader("Access-Control-Allow-Origin", request.getHeader("origin"));
        // response.setHeader("Access-Control-Allow-Headers",
        // "Access-Control-Allow-Headers, Access-Control-Allow-Methods, Access-Control-Allow-Origin, Content-Type,
        // x-xsrf-token, authorization");
        //
        // response.setHeader("Access-Control-Allow-Methods", "POST, GET, PATCH, DELETE, PUT, OPTIONS");
        //
        // // 暴露哪些头部信息（因为跨域访问默认不能获取全部头部信息）给前端VUE
        // // 目前response的token字段允许前端获取，以便后续发送的时候携带该字段进行鉴权
        // response.setHeader("Access-Control-Expose-Headers", SecurityConstants.HEADER);

        // TODO:如再遇到前端报The value of the 'Access-Control-Allow-Origin' header in the
        // response must not be the wildcard '*' when the request's credentials mode is
        // 'include'. The credentials mode of requests initiated by t
        // 则删除该行，并将前端的axios.defaults.withCredentials = true取消
        // response.setHeader("Access-Control-Allow-Credentials", "true");
        // 请求头为 accessToken
        // 请求体为 Bearer token

        String authHeader = request.getHeader(SecurityConstants.HEADER);

        if (authHeader != null && authHeader.startsWith(SecurityConstants.TOKEN_SPLIT)) {
            final String authToken = authHeader.substring(SecurityConstants.TOKEN_SPLIT.length());

            if (!CommonUtil.isEmpty(authToken)) {
                String userName = JwtTokenUtil.getUsernameFromToken(authToken);

                // int userId = jwtTokenUtil.getUserIdFromToken(authToken);

                if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // 从已有的user缓存中取了出user信息
                    CustomUserDetail user = userServiceImpl.findUserInfo(userName);

                    // 检查token是否有效
                    if (JwtTokenUtil.validateToken(authToken, user)) {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                user, null, user.getAuthorities());

                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        // 设置用户登录状态
                        // log.info("authenticated user [{}], setting security context", userName);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }

                }

            }

        }

        filterChain.doFilter(request, response);

    }

}

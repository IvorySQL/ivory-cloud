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

package com.highgo.cloud.auth.authentication;

import com.highgo.cloud.auth.util.JwtTokenUtil;
import com.highgo.cloud.constant.SecurityConstants;
import com.highgo.cloud.auth.model.CustomUserDetail;
import com.highgo.cloud.auth.response.SuccessResponse;
import com.highgo.cloud.auth.response.WriteResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description: 自定义登录成功Handler，返回json
 * @Author: yanhonghai
 * @Date: 2019/4/15 0:20
 */
@Component
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
            Authentication authentication) throws IOException, ServletException {

        CustomUserDetail principal = (CustomUserDetail) authentication.getPrincipal();
        String token = JwtTokenUtil.generateToken(principal);

        httpServletResponse.addHeader(SecurityConstants.HEADER, token);
        httpServletResponse.setContentType("application/json;charset=utf-8");

        SuccessResponse successResponse = new SuccessResponse(principal);
        WriteResponse.write(httpServletResponse, successResponse);
    }
}

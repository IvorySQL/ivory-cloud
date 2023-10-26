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

import com.alibaba.fastjson.JSONObject;
import com.highgo.cloud.auth.response.ErrorResponse;
import com.highgo.cloud.auth.response.GlobalResponseCode;
import com.highgo.cloud.auth.response.RestResponse;
import com.highgo.cloud.auth.response.WriteResponse;
import com.highgo.cloud.auth.util.Result;
import com.highgo.cloud.auth.util.ResultUtil;
import com.highgo.cloud.result.ResultCode;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Description: 自定义登录失败Handler，返回json
 * @Author: yanhonghai
 * @Date: 2019/4/15 0:22
 */
@Component
public class MyAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
            AuthenticationException e) throws IOException, ServletException {
        httpServletResponse.setContentType("application/json;charset=utf-8");
        PrintWriter out = httpServletResponse.getWriter();
        StringBuffer sb = new StringBuffer();
        Result result = new Result();
        if (e instanceof UsernameNotFoundException || e instanceof BadCredentialsException) {
            result = ResultUtil.error(ResultCode.ERROR.getCode(), "登录失败，用户名或密码输入错误!");
        } else if (e instanceof DisabledException) {
            result = ResultUtil.error(ResultCode.ERROR.getCode(), "登录失败，账户被禁用，请联系管理员!");
        } else {
            result = ResultUtil.error(ResultCode.ERROR.getCode(), "登录失败:" + e.fillInStackTrace());
        }
        out.write(JSONObject.toJSONString(result));
        out.flush();
        out.close();

        GlobalResponseCode code;

        if (e instanceof BadCredentialsException || e instanceof UsernameNotFoundException) {
            code = GlobalResponseCode.USERNAME_OR_PASSWORD_ERROR;
        } else if (e instanceof LockedException) {
            code = GlobalResponseCode.ACCOUNT_LOCKED_ERROR;
        } else if (e instanceof CredentialsExpiredException) {
            code = GlobalResponseCode.CREDENTIALS_EXPIRED_ERROR;
        } else if (e instanceof AccountExpiredException) {
            code = GlobalResponseCode.ACCOUNT_EXPIRED_ERROR;
        } else if (e instanceof DisabledException) {
            code = GlobalResponseCode.ACCOUNT_DISABLED_ERROR;
        } else {
            code = GlobalResponseCode.LOGIN_FAILED_ERROR;
        }
        RestResponse response = new ErrorResponse(code);
        WriteResponse.write(httpServletResponse, response);
    }
}

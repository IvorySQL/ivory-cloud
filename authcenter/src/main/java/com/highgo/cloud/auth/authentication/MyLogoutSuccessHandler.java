package com.highgo.cloud.auth.authentication;

import com.highgo.cloud.auth.model.CustomUserDetail;
import com.highgo.cloud.auth.response.SuccessResponse;
import com.highgo.cloud.auth.response.WriteResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * @Description: 自定义注销Handler，返回json
 * @Author: yanhonghai
 * @Date: 2019/5/4 11:22
 */
@Component
@Slf4j
public class MyLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        httpServletResponse.setHeader("Access-Control-Allow-Origin", httpServletRequest.getHeader("origin"));
        if (authentication != null) {
            CustomUserDetail userDetail = (CustomUserDetail)authentication.getPrincipal();
            log.debug("User: " + userDetail.getUsername() + " logout at " + new Date());
        }

        WriteResponse.write(httpServletResponse, new SuccessResponse());
    }
}

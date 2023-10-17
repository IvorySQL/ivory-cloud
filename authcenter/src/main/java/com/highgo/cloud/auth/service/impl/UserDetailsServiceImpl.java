package com.highgo.cloud.auth.service.impl;

import javax.annotation.Resource;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.highgo.cloud.auth.model.CustomUserDetail;

import lombok.extern.slf4j.Slf4j;


/**
 * @Description: 自定义UserDetailsService
 * @Author: yanhonghai
 * @Date: 2019/4/12 14:44
 */

/**
 * 1)执行登录的过程中，这个方法将根据用户名去查找用户，
 * 如果用户不存在，则抛出UsernameNotFoundException异常,验证成功便返回用户所属的UserDetails信息
 * 2) UserDetails中定义了用户的账户、密码、权限等信息，可通过实现该接口中的方式自行定义用户信息类
 * UserDetails代表用户信息，即主体，相当于Shiro中的Subject。org.springframework.security.core.userdetails.User是它的一个实现。
 * 3)执行登录的过程中：security内部会校验(check方法)输入的密码和查询得到的用户的密码
 */

@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
    @Resource()
    UserServiceImpl userServiceImpl;

    @Override
    public CustomUserDetail loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("The login user name: ", username);

        /**
         *  查询db获取用户角色及权限
         *  db中的用户密码已用BCryptPasswordEncoder加密
         */
       CustomUserDetail userDetails = userServiceImpl.findUserInfo(username);
        if (userDetails == null) {
            log.error("Username :" + username + ",not found");
            throw new UsernameNotFoundException("Username :" + username + ",not found");
        }
//        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
//
//        //该用户的
//        ArrayList<String> userRoles = (ArrayList<String>) userDetails.getRoles();
//
//        if (null != userRoles) {
//            for (String role : userRoles) {
//                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
//                authorities.add(authority);
//            }
//
//        }
        log.info("Successed login, the detail user info: ", JSONObject.toJSONString(userDetails));

        return userDetails;
    }
}
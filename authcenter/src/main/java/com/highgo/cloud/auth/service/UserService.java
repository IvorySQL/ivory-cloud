package com.highgo.cloud.auth.service;

import com.highgo.cloud.auth.model.CustomUserDetail;

/**
 * @Description: TODO
 * @Author: yanhonghai
 * @Date: 2018/9/19 10:03
 */
public interface UserService {


    CustomUserDetail findUserInfo(String userName);

}

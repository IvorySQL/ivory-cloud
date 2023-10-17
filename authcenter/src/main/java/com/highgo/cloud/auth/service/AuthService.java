package com.highgo.cloud.auth.service;


import com.highgo.cloud.auth.model.dto.RegisterUserDTO;
import com.highgo.cloud.auth.model.dto.UpdateUserDTO;
import com.highgo.cloud.auth.model.dto.UserEmailDTO;
import com.highgo.cloud.model.CommonResult;


/**
 * @author: highgo-lucunqiao
 * @date: 2023/8/24 10:15
 * @Description: auth
 */
public interface AuthService {



    CommonResult checkVerificationCode(String userName, String verificationCode);

    void userRegister(RegisterUserDTO registerUserDTO);


    void updateUserInfo(UpdateUserDTO updateUserDTO);

    void sendEmailVerificationCode(UserEmailDTO userEmailDTO);

    void sendEmailVerificationCodeForRegister(UserEmailDTO userEmailDTO);

 //   UserDTO findUser(String username);

 //   List<ProviderDTO> findProvider();
}

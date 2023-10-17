package com.highgo.cloud.auth.controller;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.highgo.cloud.auth.model.dto.RegisterUserDTO;
import com.highgo.cloud.auth.model.dto.UpdateUserDTO;
import com.highgo.cloud.auth.model.dto.UserEmailDTO;
import com.highgo.cloud.auth.service.AuthService;
import com.highgo.cloud.result.R;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author: highgo-lucunqiao
 * @date: 2023/9/5 10:52
 * @Description:
 */
@RestController
@Validated
@RequestMapping("${common.request-path-prefix}/v1")
@Api(tags = "授权管理")
public class AuthController {
    @Resource
    private AuthService authService;

//    @GetMapping(path = "/getUserDetail")
//    @ApiOperation("获取用户信息")
//    UserDTO findUserInfo(@RequestParam(value = "username") String username){
//        return authService.findUser(username);
//    }

//    @GetMapping(path = "/getProvider")
//    @ApiOperation("获取供应商")
//    List<ProviderDTO> findProvider(){
//        return authService.findProvider();
//    }

    @RequestMapping(value="/userRegister", method = RequestMethod.POST)
    @ApiOperation("用户注册")
    //@ReqLog("用户注册")
    public R userRegister(@RequestBody RegisterUserDTO registerUserDTO) {
        authService.userRegister(registerUserDTO);
        return R.success("注册成功！");    }


    @ApiOperation(value = "用户登出")
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    //@ReqLog("用户登出")
    public R userLogout() {
        return R.success("登出成功！");
    }

    @ApiOperation(value = "忘记密码")
    //@ReqLog("忘记密码")
    @RequestMapping(value = "/updateUserInfo", method = RequestMethod.POST)
    public R updateUserInfo(@RequestBody UpdateUserDTO udpateUserDTO) {
        authService.updateUserInfo(udpateUserDTO);
        return R.success("修改密码成功！");
    }

    @RequestMapping(value = "/emailVerificationCode", method = RequestMethod.POST)
    public R emailVerificationCode(@Valid @RequestBody UserEmailDTO userEmailDTO){
        authService.sendEmailVerificationCode(userEmailDTO);
        return R.success("验证码发送成功！");
    }

    @RequestMapping(value = "/emailVerificationCodeForRegister", method = RequestMethod.POST)
    public R emailVerificationCodeForRegister(@Valid @RequestBody UserEmailDTO userEmailDTO){
        authService.sendEmailVerificationCodeForRegister(userEmailDTO);
        return R.success("验证码发送成功！");
    }

}

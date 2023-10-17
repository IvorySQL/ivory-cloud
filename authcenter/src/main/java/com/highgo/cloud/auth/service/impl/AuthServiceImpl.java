package com.highgo.cloud.auth.service.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.highgo.cloud.auth.constant.UserConstants;
import com.highgo.cloud.auth.entity.EmailVerificationCode;
import com.highgo.cloud.auth.entity.User;
import com.highgo.cloud.auth.model.dto.RegisterUserDTO;
import com.highgo.cloud.auth.model.dto.UpdateUserDTO;
import com.highgo.cloud.auth.model.dto.UserEmailDTO;
import com.highgo.cloud.auth.repository.AccountRepository;
import com.highgo.cloud.auth.repository.VerificationCodeRepository;
import com.highgo.cloud.auth.service.AuthService;
import com.highgo.cloud.auth.service.EmailService;
import com.highgo.cloud.auth.util.UserEncryptUtil;
import com.highgo.cloud.model.CommonResult;
import com.highgo.cloud.util.BeanUtil;


import lombok.extern.slf4j.Slf4j;

/**
 * @author: highgo-lucunqiao
 * @date: 2023/8/24 10:15
 * @Description:
 */
@Service
@Slf4j
public class AuthServiceImpl implements AuthService {
    //user表操作类
    @Resource(name = "accountRepository")
    private AccountRepository accountRepository;



    //验证码表操作类
    @Resource(name = "verificationCodeRepository")
    private VerificationCodeRepository verificationCodeRepository;

    //发邮件
    @Resource(name = "emailServiceImpl")
    private EmailService emailService;



    @Override
//    public CommonResult checkVerificationCode(RegisterUserDTO registerUserDTO) {
    public CommonResult checkVerificationCode(String userName, String verificationCode) {

     //   String verificationCode = registerUserDTO.getVerificationCode();
        //获取用户30分钟之内的验证码（未使用过的）
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime before30Time = now.minusMinutes(30);
        List<EmailVerificationCode> verificationCodes = verificationCodeRepository.findVerificationCodeByUserName(userName,
                Timestamp.valueOf(df.format(now)),
                Timestamp.valueOf(df.format(before30Time)));

        if (CollectionUtils.isEmpty(verificationCodes))
            //没有验证码
            return CommonResult
                    .builder()
                    .message("未找到有效验证码，请重新申请！")
                    .result(false)
                    .build();

        //降序，取最近时间的验证码
        Collections.sort(verificationCodes, Comparator.comparing(EmailVerificationCode::getCreateTime, Comparator.reverseOrder()));
        EmailVerificationCode code = verificationCodes.get(0);

        //验证码不匹配
        if (!code.getVerificationCode().equals(verificationCode))
            return CommonResult
                    .builder()
                    .message("验证码错误！")
                    .result(false)
                    .build();

        //验证码超出有效时间
        LocalDateTime effectiveTime = code.getCreateTime().toLocalDateTime().plusMinutes(2);
        if (now.isAfter(effectiveTime))
            //超出有效时间
            return CommonResult
                    .builder()
                    .message("验证码已经失效，请重新申请！")
                    .result(false)
                    .build();

        code.setIsUsed(1);
        verificationCodeRepository.save(code);

        return CommonResult
                .builder()
                .result(true)
                .build();
    }

    @Override
    @Transactional(readOnly = false)
    public void userRegister(RegisterUserDTO registerUserDTO) {
        User byName = accountRepository.findByName(registerUserDTO.getName());
        if (byName != null) throw new RuntimeException("用户已存在，请勿重复注册！");
        CommonResult commonResult = checkVerificationCode(registerUserDTO.getName(), registerUserDTO.getVerificationCode());
        if (!commonResult.isResult()) throw new RuntimeException(commonResult.getMessage());
        User user = new User();
        user.setRole(UserConstants.AUTHORITY_ROUTINE_ID);
        BeanUtil.copyNotNullProperties(registerUserDTO, user);
        user.setRegTime(new Timestamp((new Date()).getTime()));
        user.setPassword(UserEncryptUtil.encodePwd(user.getPassword()));
        accountRepository.save(user);
    }


    @Override
    @Transactional(readOnly = false)
    public void updateUserInfo(UpdateUserDTO updateUserDTO) {
        User user = accountRepository.findByName(updateUserDTO.getName());
        if (user == null) throw new RuntimeException("用户不存在，请检查！");
        CommonResult commonResult = checkVerificationCode(updateUserDTO.getName(), updateUserDTO.getVerificationCode());
        if (!commonResult.isResult()) throw new RuntimeException(commonResult.getMessage());

        BeanUtil.copyNotNullProperties(updateUserDTO, user);
        user.setPassword(UserEncryptUtil.encodePwd(user.getPassword()));
        try {
        	 accountRepository.save(user);
		} catch (Exception e) {
			log.error("update failed.", e);
			
		}
       
    }

    @Override
    public void sendEmailVerificationCode(UserEmailDTO userEmailDTO) {
        User user = accountRepository.findByName(userEmailDTO.getUserName());
        if (user == null) throw new RuntimeException("用户不存在，请检查！");
        if (!userEmailDTO.getUserEmail().equals(user.getEmail())) throw new RuntimeException("用户邮箱不匹配！");
        //生成验证码
        String verificationCode = (Math.random() + "").substring(2, 8);
        EmailVerificationCode code = EmailVerificationCode
                .builder()
                .verificationCode(verificationCode)
                .userEmail(userEmailDTO.getUserEmail())
                .userId(user.getId())
                .userName(user.getName())
                .isUsed(0)
                .build();
        //发送验证码
        emailService.sendEmailVerificationCode(code);
        code.setCreateTime(new Timestamp((new Date()).getTime()));
        verificationCodeRepository.save(code);
    }

    @Override
    public void sendEmailVerificationCodeForRegister(UserEmailDTO userEmailDTO) {
        User byName = accountRepository.findByName(userEmailDTO.getUserName());
        if (byName != null) throw new RuntimeException("用户已存在，请勿重复注册！");
        //生成验证码
        String verificationCode = (Math.random() + "").substring(2, 8);
        EmailVerificationCode code = EmailVerificationCode
                .builder()
                .verificationCode(verificationCode)
                .userEmail(userEmailDTO.getUserEmail())
                .userName(userEmailDTO.getUserName())
                .isUsed(0)
                .build();

        //发送验证码
        emailService.sendEmailVerificationCode(code);
        code.setCreateTime(new Timestamp((new Date()).getTime()));
        verificationCodeRepository.save(code);
    }

//    @Override
//    public UserDTO findUser(String username) {
//        User byName = accountRepository.findByName(username);
//        UserDTO userDTO = new UserDTO();
//        BeanUtil.copyNotNullProperties(byName,userDTO);
//        return userDTO;
//    }

//    @Override
//    public List<ProviderDTO> findProvider() {
//        List<Provider> all = providerRepository.findAll();
//        List<ProviderDTO> providerDTOs = new ArrayList<>();
//        for(Provider p : all){
//            ProviderDTO providerDTO = new ProviderDTO();
//            BeanUtil.copyNotNullProperties(p,providerDTO);
//            providerDTOs.add(providerDTO);
//        }
//        return providerDTOs;
//    }
}

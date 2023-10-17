package com.highgo.cloud.auth.service;


import com.highgo.cloud.auth.entity.EmailVerificationCode;

/**
 * @author: highgo-lucunqiao
 * @date: 2023/3/8 9:58
 * @Description: 邮件相关service
 */
public interface EmailService {

    void sendEmailVerificationCode(EmailVerificationCode code);

}

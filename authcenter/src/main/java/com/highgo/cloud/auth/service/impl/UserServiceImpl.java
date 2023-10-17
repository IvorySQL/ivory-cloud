package com.highgo.cloud.auth.service.impl;



import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.highgo.cloud.auth.entity.User;
import com.highgo.cloud.auth.entity.UserRole;
import com.highgo.cloud.auth.model.CustomUserDetail;
import com.highgo.cloud.auth.model.HgAuthority;
import com.highgo.cloud.auth.repository.AccountRepository;

import com.highgo.cloud.auth.repository.UserRoleRepository;
import com.highgo.cloud.auth.repository.VerificationCodeRepository;
import com.highgo.cloud.auth.service.EmailService;
import com.highgo.cloud.auth.service.UserService;


/**
 * @Description: TODO
 * @Author: yanhonghai
 * @Date: 2018/9/17 0:54
 */
@Component("userServiceImpl")
public class UserServiceImpl implements UserService {
    
    //user表操作类
    @Resource(name = "accountRepository")
    private AccountRepository accountRepository;



    //验证码表操作类
    @Resource(name = "verificationCodeRepository")
    private VerificationCodeRepository verificationCodeRepository;

    //发邮件
    @Resource(name = "emailServiceImpl")
    private EmailService emailService;
    
    /**
     * 用户角色操作类
     */
    @Resource(name = "userRoleRepository")
    private UserRoleRepository userRoleRepository;
 
    @Override
	public CustomUserDetail findUserInfo(String userName) {
		User userSrc = accountRepository.findByName(userName);

		if (null != userSrc) {			
			List<HgAuthority> hgAuthorityList = new ArrayList<HgAuthority>();
			HgAuthority hgAuthority = new HgAuthority();

			
			UserRole userRole = userRoleRepository.findById(userSrc.getRole());
			// 根据用户type,set authority
			// hgAuthority.setAuthority(UserConstants.AUTHORITY);
			hgAuthority.setAuthority(userRole.getName());
			hgAuthorityList.add(hgAuthority);

			CustomUserDetail customUserDetail = new CustomUserDetail(userName, userSrc.getPassword().trim(),
					hgAuthorityList);

			List<String> roles = new ArrayList<String>();
			// 根据用户type,set role
			// roles.add(UserConstants.ROLE);
			// 增加云服务商类型，用于前端展示导航栏
			  // 增加云服务商类型，用于前端展示导航栏
//	        List<Provider> allProviders = providerRepository.findAll();
//	        if (!CommonUtil.isEmpty(allProviders)) {
//	            for (Provider provider : allProviders) {
//	                if (null != provider.getKind()) {
//	                    roles.add(ProviderTypeEnum.getName(provider.getKind()));
//	                }
//	            }
//	        }
			roles.add(userRole.getName());
			customUserDetail.setRoles(roles);

			customUserDetail.setUserId(userSrc.getId());
			return customUserDetail;
		} else {
			return null;
		}


	}
}

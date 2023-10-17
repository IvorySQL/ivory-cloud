package com.highgo.cloud.auth.config;


import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;

import com.highgo.cloud.auth.authentication.MyAuthenticationFailureHandler;
import com.highgo.cloud.auth.authentication.MyAuthenticationSuccessHandler;
import com.highgo.cloud.auth.authentication.MyLogoutSuccessHandler;
import com.highgo.cloud.auth.constant.UrlConstants;
import com.highgo.cloud.auth.constant.UserConstants;
import com.highgo.cloud.auth.service.impl.UserDetailsServiceImpl;

import lombok.extern.slf4j.Slf4j;


/**
 * @Description: 由于security是由UsernamePasswordAuthenticationFilter这个类定义登录的,
 * 里面默认是/login路径,我们要让他用我们的/mylogin路径,就需要配置.loginProcessingUrl("/mylogin")
 * @Author: yanhonghai
 * @Date: 2019/4/13 15:48
 */
//开启security
@Configuration
//保证post之前的注解可以使用
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
//@ConfigurationProperties(prefix = "security")
@Slf4j
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 配置了自定义登入登出Handler，优先响应登入登出Handler，
     * 这里是返回json给前端处理，后端重定向设置不起效果
     */
    @Autowired
    MyAuthenticationFailureHandler myAuthenticationFailureHandler;
    @Autowired
    MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;
    @Autowired
    MyLogoutSuccessHandler myLogoutSuccessHandle;
    @Autowired
    UserDetailsServiceImpl myUserDetailsService;
    @Autowired
    private DataSource dataSource;
    
    @Value("${common.request-path-prefix}")
    private String request_path_prefix;


    @Value("${security.ignore.static}")
    private String[] securityIgnoreStatic;

    @Value("${security.ignore.api}")
    private String[] securityIgnoreAPI;
    
    @Autowired
    private CustomJwtAuthenticationTokenFilter customJwtAuthenticationTokenFilter;

    
    /**
     * 用户发送请求到UsernamePasswordAuthenticationFilter，
     * 当用户认证成功以后，会调一个RemeberMeService这样一个服务。
     * 这个服务里面有一个TokenRepository，会生成一个Token，将这个Token写入到浏览器的Cookie里面，
     * 同时TokenRepository把生成的Token写入到数据库里面（还有用户名）
     * springSecurity会根据情况自动将token插入persistent_logins
     * .antMatchers("/admin/**").access("hasRole('ADMIN') and hasIpAddress('123.123.123.123')") // pass SPEL using access method
     * 再次访问需要权限的资源时：用cookie中的加密串，到db中验证，如果通过，自动登录才算通过
     *

    /**
     * //持久化token存储 //数据库的表必须是persistent_logins
     * ，字段必须是username、series(序列号)、token、last_used（更新时间）
     */
    @Bean
    public JdbcTokenRepositoryImpl tokenRepository() {
        JdbcTokenRepositoryImpl jr = new JdbcTokenRepositoryImpl();
        jr.setDataSource(dataSource);
        return jr;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(myUserDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //将我们自定义的过滤器，配置到UsernamePasswordAuthenticationFilter之前
        // 使用jwt的Authentication,来解析过来的请求是否有token
        http.addFilterBefore(customJwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
    	
     // 防止iframe 造成跨域
        http.headers()
        .frameOptions()
        .disable();
     // 禁用缓存
        http.headers().cacheControl();

        log.debug("enter webSecurityConfig.........");

        String[] securityIgnoreResource = joinSecurityIgnoreResource(securityIgnoreStatic, securityIgnoreAPI);

        for (int i = 0; i < securityIgnoreResource.length; i++) {
            log.debug("The url [{}] which don't need auth: [{}]", i, securityIgnoreResource[i]);
        }
        
        //完整的登录URL
		String completeLoginUrl = request_path_prefix + UrlConstants.VERSION + UrlConstants.LOGIN_URL;

		//完整的退出URL
        String completeLogoutUrl = request_path_prefix + UrlConstants.VERSION + UrlConstants.LOGOUT_URL;
        


        //用户可以访问的URl
        String routineAcessUrl = request_path_prefix + UrlConstants.VERSION  + "/**";


        http
                .authorizeRequests()
                .antMatchers(securityIgnoreResource).permitAll()//定义不需要认证就可以访问
              //  .antMatchers("/**").permitAll()//定义不需要认证就可以访问
                .antMatchers(HttpMethod.OPTIONS, "/**").anonymous()
                //定义需要相应角色就可以访问，角色信息可以自定义，在sys_role表中存储
                //和在接口中使用注解同样效果hasAuthority 等同于hasRole,校验时角色将被增加 "ROLE_"

                .antMatchers("/websocket/**").permitAll()
                .antMatchers(routineAcessUrl).hasAnyAuthority(UserConstants.ROLE_ADMIN_NAME, UserConstants.ROLE_ROUTINE_NAME)
                .anyRequest().authenticated()//其余所有请求都需要登录认证才能访问
                .and()
                .formLogin()
                //指定url，可由相应的controller处理跳转到登录页如login_page.html
                .loginPage("/index.html")//自定义登录url
                //指定自定义form表单请求的路径
                .loginProcessingUrl(completeLoginUrl).usernameParameter(UrlConstants.LOGIN_FORM_USERNAME_KEY).passwordParameter(UrlConstants.LOGIN_FORM_PASSWORD_KEY)
                //自定义认证成功或者失败的返回json
                .successHandler(myAuthenticationSuccessHandler)
                .failureHandler(myAuthenticationFailureHandler)
                .permitAll()
                .and()
                .logout()
                .logoutUrl(completeLogoutUrl)//自定义退出url
                .logoutSuccessHandler(myLogoutSuccessHandle)//设置了登入登出的Handler,优先响应Handler
                .invalidateHttpSession(true)
                .permitAll()
                .and()
                .rememberMe()// 记住我
                .rememberMeParameter("rememberMe")
                .userDetailsService(myUserDetailsService).tokenValiditySeconds(60 * 60 * 24);
        //默认都会产生一个hiden标签 里面有安全相关的验证 防止请求伪造 这边我们暂时不需要 可禁用掉

       // http.cors().and().csrf().disable();
        http.cors().disable();
        http.csrf().disable();

        //因为我们要使用jwt托管安全信息，所以把Session禁止掉
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    /**
     * 密码加密的bean
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
     // 配置密码加密，这里声明成bean，方便注册用户时直接注入
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
    }

    /**
     * 将不需要鉴权的静态资源，与api资源合并
     * @param securityIgnoreStatic
     * @param securityIgnoreAPI
     * @return
     */
    private String[] joinSecurityIgnoreResource(String[] securityIgnoreStatic, String[] securityIgnoreAPI) {

    	//给api 添加 request prefix
    	addCommonRequestPrefixForApi(securityIgnoreAPI);


    	String[] securityIgnoreResource = new String[securityIgnoreStatic.length + securityIgnoreAPI.length];


    	System.arraycopy(securityIgnoreStatic, 0, securityIgnoreResource, 0, securityIgnoreStatic.length);


    	System.arraycopy(securityIgnoreAPI, 0, securityIgnoreResource, securityIgnoreStatic.length, securityIgnoreAPI.length);

    	return securityIgnoreResource;
    }

    private void addCommonRequestPrefixForApi(String[] securityIgnoreAPI) {
    	String commonRequestPrefix =  request_path_prefix + UrlConstants.VERSION;

    	for (int i = 0; i < securityIgnoreAPI.length; i++) {
    		securityIgnoreAPI[i] = commonRequestPrefix + securityIgnoreAPI[i] ;
    	}

    }
}
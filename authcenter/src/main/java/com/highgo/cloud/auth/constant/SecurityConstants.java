package com.highgo.cloud.auth.constant;

/**
 * 安全检验的常量类
 * @author chushaolin
 *
 */
public class SecurityConstants {
    //请求体为 Bearer token
    /**
     * 请求头为 accessToken
     */
    public static final String HEADER = "Authorization";

    /**
     * 前端传来的消息头
     */
    public static final String HEADER_FE = "X-CSRF-TOKEN";

    public static final String TOKEN_SPLIT = "bearer ";
}

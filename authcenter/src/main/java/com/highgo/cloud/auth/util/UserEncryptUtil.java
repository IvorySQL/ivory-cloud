package com.highgo.cloud.auth.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserEncryptUtil {
    public static BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public static String encodePwd(String password) {
        return encoder.encode(password);
    }
}

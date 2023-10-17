package com.highgo.cloud.auth.config;

import com.highgo.cloud.util.DESUtil;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 邮箱密码加密用的
 * @author: highgo-lucunqiao
 * @date: 2023/7/21 11:07
 * @Description: jasypt config
 */
@Configuration
public class JasyptConfig {
    private static final String salt = "c6b1f3c1a07b519243535dc842ace03a";

    @Bean("jasyptStringEncryptor")
    public StringEncryptor stringEncryptor() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(DESUtil.decrypt(salt));
        config.setPoolSize("1");
        config.setStringOutputType("base64");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.salt.NoOpIVGenerator");
        config.setKeyObtentionIterations(1000);
        config.setAlgorithm("PBEWithMD5AndDES");
        encryptor.setConfig(config);
        return encryptor;
    }
}

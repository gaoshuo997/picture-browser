package com.jimmy.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class JwtConfig {

    // getter 方法（供 JwtUtil 调用）
    /**
     * JWT 密钥（重要：生产环境需配置在环境变量/配置中心，不可明文提交到代码仓库）
     * 密钥长度建议：HS256 算法至少 32 位字符
     */
    @Value(value = "${jwt.secret-key}")
    private String secretKey;

    /**
     * JWT 过期时间（单位：毫秒），此处配置为 2 小时（7200000 ms）
     */
    @Value("${jwt.expire-time:7200000}")
    private long expireTime;

    /**
     * JWT 签发者
     */
    private final String issuer = "jimmy-su";

}

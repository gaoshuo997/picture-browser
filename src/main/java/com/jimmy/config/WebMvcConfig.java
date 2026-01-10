package com.jimmy.config;

import com.jimmy.common.intercepter.LoginValidatorInterceptor;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Resource
    private LoginValidatorInterceptor loginValidatorInterceptor;

    @Override
    public void addInterceptors(@Nonnull InterceptorRegistry registry) {
        registry.addInterceptor(loginValidatorInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/pic/sign-up","/pic/auth-token");
    }
}

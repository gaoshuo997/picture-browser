package com.jimmy.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jimmy.common.result.Result;
import com.jimmy.common.result.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * JWT 认证入口点
 * 处理未认证用户访问受保护资源的情况
 * 当用户未登录或 Token 无效时触发
 */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 处理认证失败请求
     * 当匿名用户尝试访问需要认证的资源时调用此方法
     */
    @Override
    public void commence(HttpServletRequest request,
                        HttpServletResponse response,
                        AuthenticationException authException) throws IOException {
        // 记录认证失败日志
        log.warn("认证失败 - URI: {}, 错误信息: {}",
                request.getRequestURI(),
                authException.getMessage());

        // 设置响应内容类型为 JSON
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        // 设置字符编码
        response.setCharacterEncoding("UTF-8");
        // 设置 HTTP 状态码为 401（未授权）
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // 构建统一响应结果
        Result<?> result = Result.failed(ResultCode.UNAUTHORIZED, authException.getMessage());

        // 将结果写入响应输出流
        objectMapper.writeValue(response.getOutputStream(), result);
    }
}
package com.jimmy.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jimmy.common.result.Result;
import com.jimmy.common.result.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * JWT 访问拒绝处理器
 * 处理已认证用户访问无权限资源的情况
 * 当已登录用户尝试访问没有权限的资源时触发
 */
@Slf4j
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 处理访问被拒绝的请求
     * 当已认证用户尝试访问没有权限的资源时调用此方法
     */
    @Override
    public void handle(HttpServletRequest request,
                      HttpServletResponse response,
                      AccessDeniedException accessDeniedException) throws IOException {
        // 记录访问拒绝日志
        log.warn("访问被拒绝 - URI: {}, 用户: {}, 错误信息: {}",
                request.getRequestURI(),
                request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "anonymous",
                accessDeniedException.getMessage());

        // 设置响应内容类型为 JSON
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        // 设置字符编码
        response.setCharacterEncoding("UTF-8");
        // 设置 HTTP 状态码为 403（禁止访问）
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        // 构建统一响应结果
        Result<?> result = Result.failed(ResultCode.FORBIDDEN,ResultCode.FORBIDDEN.getMessage());

        // 将结果写入响应输出流
        objectMapper.writeValue(response.getOutputStream(), result);
    }
}
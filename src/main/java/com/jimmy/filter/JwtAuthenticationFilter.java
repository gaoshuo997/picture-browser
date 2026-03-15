package com.jimmy.filter;

import com.jimmy.common.exception.ErrorMsg;
import com.jimmy.common.result.BusinessException;
import com.jimmy.jwt.JwtTokenProvider;
import com.jimmy.service.BlacklistService;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 认证过滤器
 * 拦截每个请求，验证 JWT Token 的有效性
 * 继承 OncePerRequestFilter 确保每个请求只执行一次
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Resource
    private JwtTokenProvider jwtTokenProvider;

    @Resource
    private UserDetailsService userDetailsService;

    @Resource
    private BlacklistService blacklistService;

    /**
     * 过滤器核心方法
     * 从请求头中提取 JWT Token，验证并设置认证信息到 SecurityContext
     */
    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request,
                                    @Nonnull HttpServletResponse response,
                                    @Nonnull FilterChain filterChain) throws ServletException, IOException {
        try {
            // 1. 从请求中提取 JWT Token
            String jwt = jwtTokenProvider.resolveToken(request);

            // 2. 获取当前请求的 URI，用于日志记录
            String requestURI = request.getRequestURI();

            // 3. 检查 Token 是否有效
            if (!StringUtils.hasText(jwt)){
                filterChain.doFilter(request, response);
                return;
            }

            // 检查用户是否进入黑名单
            if (blacklistService.isBlacklisted(jwt)){
                throw new BusinessException(ErrorMsg.EXIST_IN_BLACKLIST.getMsg());
            }

            if (!jwtTokenProvider.validateToken(jwt)){
                log.warn("JWT Token 验证失败");
                // 这里可以抛异常，或者直接返回 401 JSON
                // 建议直接写入响应并返回，避免抛出 ServletException 导致进入容器错误页
                throw new BusinessException(ErrorMsg.TOKEN_CHECK_ERROR.getMsg());
            }

            // 3.1 从 Token 中提取用户ID
            Long userId = jwtTokenProvider.getUserId(jwt);

            // 3.2 检查当前用户是否已通过认证
            // SecurityContextHolder 存储了当前线程的安全上下文
            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 3.3 从 UserDetailsService 加载用户详情
                // 这里会查询数据库获取用户信息和权限
                UserDetails userDetails = userDetailsService.loadUserByUsername(userId.toString());

                // 3.4 创建认证令牌
                // UsernamePasswordAuthenticationToken 是 Spring Security 的标准认证令牌
                UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                        userDetails,           // 用户信息
                        null,                  // 凭证（已验证过，设为 null）
                        userDetails.getAuthorities()  // 用户权限列表
                    );

                // 3.5 设置请求详情（记录 IP、SessionId 等）
                authenticationToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 3.6 将认证信息存入 SecurityContext
                // 此后当前线程的所有代码都可以通过 SecurityContext 获取用户信息
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                log.debug("用户认证成功 - userId: {}, authorities: {}",
                    userId, userDetails.getAuthorities());
            }

        } catch (Exception e) {
            // 捕获所有异常，记录错误日志
            log.error("JWT 认证过滤器处理异常: {}", e.getMessage(), e);
            // 清除安全上下文，防止残留认证信息
            SecurityContextHolder.clearContext();
        }

        // 4. 继续过滤器链
        // 无论认证成功或失败，都将请求传递给下一个过滤器
        filterChain.doFilter(request, response);
    }

    /**
     * 判断当前请求是否应该被当前过滤器处理
     * 返回 true 表示跳过当前过滤器（不执行 doFilterInternal）
     * 返回 false 表示执行当前过滤器
     * 这里我们让过滤器处理所有请求，但在 doFilterInternal 中根据 Token 情况决定如何处理
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // 公开接口仍然经过过滤器，只是不强制要求 Token
        // 这样可以统一处理逻辑，比如记录日志等
        // 如果希望完全跳过某些路径，可以在这里添加逻辑
        String path = request.getRequestURI();
        // 健康检查端点完全跳过认证逻辑
        return path.equals("/actuator/health") || path.equals("/actuator/info");
    }
}
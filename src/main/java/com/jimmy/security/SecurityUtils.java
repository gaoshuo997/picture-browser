package com.jimmy.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

/**
 * Spring Security 工具类
 * 提供获取当前登录用户信息的方法
 */
@Slf4j
public class SecurityUtils {

    /**
     * 获取当前认证信息
     *
     * @return Authentication 对象，可能为 null
     */
    public static Optional<Authentication> getAuthentication() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
    }

    /**
     * 获取当前登录用户的 ID
     * 从 UserDetails 的 username 中获取（存储的是用户ID）
     *
     * @return 用户ID，未登录返回 null
     */
    public static Long getCurrentUserId() {
        return getAuthentication()
                .map(authentication -> {
                    Object principal = authentication.getPrincipal();
                    if (principal instanceof UserDetails) {
                        try {
                            return Long.parseLong(((UserDetails) principal).getUsername());
                        } catch (NumberFormatException e) {
                            log.warn("无法解析用户ID: {}", ((UserDetails) principal).getUsername());
                            return null;
                        }
                    }
                    return null;
                })
                .orElse(null);
    }

    /**
     * 获取当前登录用户的 UserDetails
     *
     * @return UserDetails，未登录返回 null
     */
    public static UserDetails getCurrentUserDetails() {
        return getAuthentication()
                .map(authentication -> {
                    Object principal = authentication.getPrincipal();
                    if (principal instanceof UserDetails) {
                        return (UserDetails) principal;
                    }
                    return null;
                })
                .orElse(null);
    }

    /**
     * 检查当前用户是否已认证（已登录）
     *
     * @return true 已登录，false 未登录
     */
    public static boolean isAuthenticated() {
        return getAuthentication()
                .map(Authentication::isAuthenticated)
                .orElse(false);
    }

    /**
     * 检查当前用户是否具有指定角色
     *
     * @param role 角色名称（不需要 ROLE_ 前缀）
     * @return true 有权限，false 无权限或未登录
     */
    public static boolean hasRole(String role) {
        String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        return getAuthentication()
                .map(auth -> auth.getAuthorities().stream()
                        .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(authority)))
                .orElse(false);
    }

    /**
     * 清除当前安全上下文
     * 通常用于登出操作
     */
    public static void clearContext() {
        SecurityContextHolder.clearContext();
    }
}
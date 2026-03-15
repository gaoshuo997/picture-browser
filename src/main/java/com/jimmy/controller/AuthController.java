package com.jimmy.controller;

import com.jimmy.common.result.Result;
import com.jimmy.jwt.JwtTokenProvider;
import com.jimmy.req.LoginUserReq;
import com.jimmy.req.SignUserSave;
import com.jimmy.security.AuthenticationService;
import com.jimmy.security.SecurityUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器（新版）
 * 处理用户认证相关的请求：注册、登录、登出
 * 集成 Spring Security + JWT 实现
 */
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Resource
    private JwtTokenProvider jwtTokenProvider;

    @Resource
    private AuthenticationService authenticationService;

    /**
     * 用户注册
     *
     * @param save 注册信息
     * @return 注册结果，包含 JWT Token
     */
    @PostMapping("/register")
    public Result<Map<String, Object>> signUp(@Valid @RequestBody SignUserSave save) {
        log.info("用户注册 - loginName: {}", save.getLoginName());
        return authenticationService.register(save);
    }

    /**
     * 用户登录
     * 使用 Spring Security 进行认证
     *
     * @param req 登录请求
     * @return 登录结果，包含 JWT Token
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginUserReq req) {
        return authenticationService.login(req);
    }

    /**
     * 用户登出
     *
     * @param request HTTP 请求
     * @return 登出结果
     */
    @PostMapping("/logout")
    public Result<?> logout(HttpServletRequest request) {
        log.info("用户登出");
        SecurityUtils.clearContext();
        return Result.successWithMessage("退出登录成功");
    }

    /**
     * 获取当前登录用户信息
     * 需要 USER 角色或 ADMIN 角色
     *
     * @return 当前用户信息
     */
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Result<?> getCurrentUser() {
        // 可以从 SecurityContext 获取当前用户信息
        // 这里简化处理，实际应根据业务需求返回
        return Result.successWithMessage("获取当前用户信息");
    }

    /**
     * 刷新 Token
     *
     * @param request HTTP 请求
     * @return 新的 Token
     */
    @PostMapping("/refresh")
    public Result<Map<String, Object>> refreshToken(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            String refreshedToken = jwtTokenProvider.refreshToken(token);

            if (refreshedToken != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("token", refreshedToken);
                return Result.success(result);
            }
        }

        return Result.failed("Token 刷新失败");
    }
}
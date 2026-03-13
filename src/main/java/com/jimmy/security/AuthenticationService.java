package com.jimmy.security;

import com.jimmy.common.exception.BadReqExceptionMsg;
import com.jimmy.common.result.BusinessException;
import com.jimmy.common.result.Result;
import com.jimmy.entity.SignUser;
import com.jimmy.jwt.JwtTokenProvider;
import com.jimmy.req.LoginUserReq;
import com.jimmy.req.SignUserSave;
import com.jimmy.service.SignUserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证服务
 * 处理登录、注册等认证相关操作
 */
@Slf4j
@Service
public class AuthenticationService {

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private SignUserService signUserService;

    @Resource
    private JwtTokenProvider jwtTokenProvider;

    /**
     * 用户登录
     * 使用 Spring Security 进行认证，成功后生成 JWT Token
     *
     * @param req 登录请求
     * @return 包含 JWT Token 的结果
     */
    public Result<Map<String, Object>> login(LoginUserReq req) {
        try {
            log.info("用户登录 - loginName: {}", req.getLoginName());

            // 从数据库获取用户详细信息
            SignUser signUser = signUserService.checkSignUser(req.getLoginName(), req.getPassword());

            // 生成 JWT Token
            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("userId", signUser.getId());
            extraClaims.put("loginName", signUser.getLoginName());

            String token = jwtTokenProvider.generateToken(signUser.getId(), extraClaims);

            // 构建响应结果
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("token", token);
            resultMap.put("result", true);
            resultMap.put("message", "登录成功");
            resultMap.put("user", signUser);

            log.info("用户登录成功 - userId: {}, loginName: {}", signUser.getId(), signUser.getLoginName());

            return Result.success(resultMap);

        } catch (BadCredentialsException e) {
            log.warn("登录失败 - 用户名或密码错误: {}", req.getLoginName());
            return Result.failed("用户名或密码错误");
        } catch (AuthenticationException e) {
            log.warn("登录失败 - 认证异常: {}", e.getMessage());
            return Result.failed("认证失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("登录异常", e);
            return Result.failed("登录失败: " + e.getMessage());
        }
    }

    /**
     * 用户注册
     * @param save 注册用户信息
     * @return 注册成功返回信息
     */
    public Result<Map<String, Object>> register(SignUserSave save) {
        try{
            SignUser saved = signUserService.insertSignUser(save);
            if (saved != null) {
                Map<String, Object> resultMap = new HashMap<>(4);
                Map<String, Object> extraClaims = new HashMap<>(2);
                extraClaims.put("userId", saved.getId());
                extraClaims.put("loginName", saved.getLoginName());

                String token = jwtTokenProvider.generateToken(saved.getId(), extraClaims);
                resultMap.put("token", token);
                resultMap.put("result", true);
                resultMap.put("message", "注册成功");
                resultMap.put("user", saved);

                log.info("用户注册成功 - userId: {}", saved.getId());
                return Result.success(resultMap);
            } else {
                log.warn("用户注册失败");
                return Result.failed(BadReqExceptionMsg.REGISTER_ERROR.getMessage());
            }
        } catch (Exception e) {
            throw new BusinessException(BadReqExceptionMsg.REGISTER_ERROR.getCode()
                    ,BadReqExceptionMsg.REGISTER_ERROR.getMessage());
        }

    }
}
package com.jimmy.controller;

import com.jimmy.common.result.Result;
import com.jimmy.entity.SignUser;
import com.jimmy.jwt.JwtTokenProvider;
import com.jimmy.req.LoginUserReq;
import com.jimmy.req.SignUserReq;
import com.jimmy.service.SignUserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class LoginController {

    @Resource
    private JwtTokenProvider jwtTokenProvider;

    @Resource
    private SignUserService signUserService;

    @ResponseBody
    @PostMapping(value = "/register")
    public Result<Map<String,Object>> signUp(@Valid @RequestBody SignUserReq req) {
        SignUser saved = signUserService.insertSignUser(req);
        if (saved != null){
            Map<String, Object> resultMap = new HashMap<>(4);
            Map<String, Object> extraClaims = new HashMap<>(2);
            extraClaims.put("userId", saved.getId());
            extraClaims.put("loginName", saved.getLoginName());
            String token = jwtTokenProvider.generateToken(saved.getId(), extraClaims);
            resultMap.put("token",token);
            resultMap.put("result", true);
            resultMap.put("message", "登录成功");
            resultMap.put("user", saved);

            return Result.success(resultMap);
        }else {
            return Result.failed();
        }
    }

    @ResponseBody
    @PostMapping(value = "/login")
    public Result<Map<String,Object>> login(
            @Valid @RequestBody LoginUserReq req){
        SignUser signUser = signUserService.checkSignUser(req.getLoginName(), req.getPassword());
        Map<String, Object> resultMap = new HashMap<>(4);
        if (signUser != null){
            Map<String, Object> extraClaims = new HashMap<>(2);
            extraClaims.put("userId", signUser.getId());
            extraClaims.put("loginName", signUser.getLoginName());
            String token = jwtTokenProvider.generateToken(signUser.getId(), extraClaims);
            resultMap.put("token",token);
            resultMap.put("result", true);
            resultMap.put("message", "登录成功");
            resultMap.put("user", signUser);
        }
        return Result.success(resultMap);
    }

    // token
    @ResponseBody
    @PostMapping(value = "/auth-token")
    public Result<Map<String, Object>> auth(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        Map<String, Object> content = new HashMap<>();
        content.put("result", false);
        content.put("message", "登录认证失败");
        if (token != null && jwtTokenProvider.validateToken(token)) {
            Long userId = jwtTokenProvider.getUserId(token);
            SignUser signUser = signUserService.findSignUserById(userId);
            String checksum = jwtTokenProvider.getChecksum(token);
            if (checksum != null
                    && signUser != null) {
                content.put("result", true);
                content.put("userId", userId);
                content.put("loginName", signUser.getLoginName());
                content.put("message", "登录认证成功");
            }
        }
        return Result.success(content);
    }

    @PostMapping("/logout")
    public Result<?> logout(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        // 后期改造为redis存储token
        // redisTemplate.opsForSet().add("logout", token);
        return Result.success();
    }
}

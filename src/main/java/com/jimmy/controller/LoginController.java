package com.jimmy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jimmy.common.web.ApplicationResponseEntity;
import com.jimmy.entity.SignUser;
import com.jimmy.entity.UserInfo;
import com.jimmy.jwt.JwtTokenProvider;
import com.jimmy.req.LoginUserReq;
import com.jimmy.req.SignUserReq;
import com.jimmy.service.SignUserService;
import com.jimmy.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.redis.core.StringRedisTemplate;
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
    public ApplicationResponseEntity<Map<String, Object>> signUp(@Valid @RequestBody SignUserReq req) {
        SignUser saved = signUserService.insertSignUser(req);
        Map<String, Object> resultMap = new HashMap<>(8);
        if (saved != null){
            resultMap.put("result", true);
            resultMap.put("message", "注册成功");
//            resultMap.put("user", saved);
        }else {
            resultMap.put("result", false);
            resultMap.put("message", "注册失败");
        }
        ApplicationResponseEntity<Map<String, Object>> responseEntity = new ApplicationResponseEntity<>();
        responseEntity.setData(resultMap);
        return responseEntity;
    }

    @ResponseBody
    @PostMapping(value = "/login")
    public ApplicationResponseEntity<Map<String,Object>> login(
            @RequestBody LoginUserReq req){
        SignUser signUser = signUserService.checkSignUser(req.getLoginName(), req.getPassword());
        Map<String, Object> resultMap = new HashMap<>(8);
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
        ApplicationResponseEntity<Map<String,Object>> result = new ApplicationResponseEntity<>();
        result.setData(resultMap);
        return result;
    }

    // token
    @ResponseBody
    @PostMapping(value = "/auth-token")
    public ApplicationResponseEntity<Map<String, Object>> auth(HttpServletRequest request) {
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
        ApplicationResponseEntity<Map<String, Object>> applicationResponseEntity = new ApplicationResponseEntity<>();
        applicationResponseEntity.setData(content);
        return applicationResponseEntity;
    }
}

package com.jimmy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jimmy.common.web.ApplicationResponseEntity;
import com.jimmy.entity.SignUser;
import com.jimmy.entity.UserInfo;
import com.jimmy.jwt.JwtTokenProvider;
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
public class LoginController {

    @Resource
    private JwtTokenProvider jwtTokenProvider;

    @Resource
    private SignUserService signUserService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @ResponseBody
    @PostMapping(value = "/sign-up")
    public ApplicationResponseEntity<Map<String, Object>> signUp(@Valid @RequestBody SignUserReq req) {
        SignUser saved = signUserService.insertSignUser(req);
        Map<String, Object> resultMap = new HashMap<>(8);
        if (saved != null){
            resultMap.put("result", true);
            resultMap.put("message", "注册成功");
        }else {
            resultMap.put("result", false);
            resultMap.put("message", "注册失败");
        }
        Map<String, Object> extraClaims = new HashMap<>(2);
        assert saved != null;
        extraClaims.put("userId", saved.getId());
        extraClaims.put("loginName", saved.getLoginName());
        String token = jwtTokenProvider.generateToken(saved.getId(), extraClaims);
        resultMap.put("token",token);
        ApplicationResponseEntity<Map<String, Object>> responseEntity = new ApplicationResponseEntity<>();
        responseEntity.setContent(resultMap);
        return responseEntity;
    }

    @ResponseBody
    @PostMapping(value = "login")
    public ApplicationResponseEntity<Map<String,Object>> login(
            @RequestParam(value = "loginName") String loginName,
            @RequestParam(value = "loginPassword") String loginPassword){

        ApplicationResponseEntity<Map<String,Object>> result = new ApplicationResponseEntity<>();
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
        applicationResponseEntity.setContent(content);
        return applicationResponseEntity;
    }
}

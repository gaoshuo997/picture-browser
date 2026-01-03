package com.jimmy.controller;

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
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController {

    @Resource
    private JwtTokenProvider jwtTokenProvider;

    @Resource
    private UserService userService;

    @Resource
    private SignUserService signUserService;

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

    @ResponseBody
    @RequestMapping(value = "/auth-token", method = RequestMethod.POST)
    public ApplicationResponseEntity<Map<String, Object>> auth(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        Map<String, Object> content = new HashMap<>();
        content.put("result", false);
        content.put("message", "登录认证失败");
        if (token != null && jwtTokenProvider.validateToken(token)) {
            Long userId = jwtTokenProvider.getUserId(token);
            UserInfo userInfo = userService.findById(userId);
            String checksum = jwtTokenProvider.getChecksum(token);
            if (checksum != null
                    && userInfo != null
                    && checksum.equals(userInfo.getChecksum())) {
                content.put("result", true);
                content.put("userId", userId);
                content.put("userName", userInfo.getLoginName());
//                content.put("companyId", jwtTokenProvider.getCompanyId(token));
//                content.put("actions", jwtTokenProvider.getActions(token));
                content.put("message", "登录认证成功");
            }
        }
        ApplicationResponseEntity<Map<String, Object>> applicationResponseEntity = new ApplicationResponseEntity<>();
        applicationResponseEntity.setContent(content);
        return applicationResponseEntity;
    }
}

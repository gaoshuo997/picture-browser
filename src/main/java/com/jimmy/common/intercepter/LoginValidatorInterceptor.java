package com.jimmy.common.intercepter;

import com.jimmy.common.result.Result;
import com.jimmy.common.result.ResultCode;
import com.jimmy.entity.SignUser;
import com.jimmy.jwt.JwtTokenProvider;
import com.jimmy.service.SignUserService;
import com.jimmy.utils.JacksonUtils;
import com.jimmy.utils.UserUtils;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

@Component
@Slf4j
public class LoginValidatorInterceptor implements HandlerInterceptor {

    @Resource
    private JwtTokenProvider jwtTokenProvider;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Resource
    private SignUserService signUserService;

    @Override
    public void afterCompletion(HttpServletRequest arg0,
                                HttpServletResponse arg1, Object arg2, Exception arg3){
        UserUtils.remove();
    }


    @Override
    public boolean preHandle(@Nonnull HttpServletRequest request,
                             @Nonnull HttpServletResponse response, @Nullable Object arg2) throws Exception {
        String token = jwtTokenProvider.resolveToken(request);
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        String path = requestURI.substring(contextPath.length()); // 去掉上下文路径后的纯路径
        log.info("===== 拦截到请求，路径：{}，上下文路径：{}，处理后路径：{} =====", requestURI, contextPath, path);
        if (token != null && jwtTokenProvider.validateToken(token)) {
            Long userId = jwtTokenProvider.getUserId(token);
//            String checksum = jwtTokenProvider.getChecksum(token);
            SignUser userInfo = signUserService.findSignUserById(userId);
//            List<String> actionValues = (List<String>) redisTemplate.opsForValue()
//                    .get("actionvalue_" + userId);

            if (userInfo != null) {
                UserUtils.setUserName(userInfo.getLoginName());
                UserUtils.setIp(request.getRemoteAddr());
                UserUtils.setUserId(userId);
                return true;
            }
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().print(JacksonUtils.toJson(Result.failed(ResultCode.UNAUTHORIZED)));
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        return false;
    }
}

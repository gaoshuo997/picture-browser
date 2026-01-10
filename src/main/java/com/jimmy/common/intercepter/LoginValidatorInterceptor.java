package com.jimmy.common.intercepter;

import com.jimmy.common.exception.ErrorMsg;
import com.jimmy.common.web.ActionStatus;
import com.jimmy.common.web.ApplicationErrorResponseEntity;
import com.jimmy.common.web.ApplicationResponseEntity;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

@Component
public class LoginValidatorInterceptor implements HandlerInterceptor {

    @Resource
    private JwtTokenProvider jwtTokenProvider;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Resource
    private SignUserService signUserService;


    @Override
    public boolean preHandle(@Nonnull HttpServletRequest request,
                             @Nonnull HttpServletResponse response, @Nullable Object arg2) throws Exception {
        String token = jwtTokenProvider.resolveToken(request);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            Long userId = jwtTokenProvider.getUserId(token);
            String checksum = jwtTokenProvider.getChecksum(token);
            SignUser userInfo = signUserService.findSignUserById(userId);
            List<String> actionValues = (List<String>) redisTemplate.opsForValue()
                    .get("actionvalue_" + userId);

            if (checksum != null
                    && userInfo != null) {
                UserUtils.setUserName(userInfo.getLoginName());
                UserUtils.setIp(request.getRemoteAddr());
                UserUtils.setUserId(userId);
                UserUtils.setActions(actionValues);
                return true;
            }
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ApplicationErrorResponseEntity content = new ApplicationErrorResponseEntity();
//        content.setNumericErrorCode(UnauthorizedErrorCode.NOT_LOGIN.getErrorCode());
//        content.setErrorCode(UnauthorizedErrorCode.NOT_LOGIN.getError());
        content.setMessage(ErrorMsg.NOT_LOGIN.getMsg());
        ApplicationResponseEntity<ApplicationErrorResponseEntity> result = new ApplicationResponseEntity<>();
        result.setActionStatus(ActionStatus.FAIL);
        result.setContent(content);
        response.getWriter().print(JacksonUtils.toJson(result));
        return false;
    }
}

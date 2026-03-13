package com.jimmy.security;

import com.jimmy.common.exception.BadReqExceptionMsg;
import com.jimmy.common.result.BusinessException;
import com.jimmy.constant.StatusFlag;
import com.jimmy.entity.SignUser;
import com.jimmy.resp.RoleResp;
import com.jimmy.service.SignUserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Spring Security 用户详情服务实现
 * 用于加载用户信息并构建 UserDetails 对象
 */
@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private SignUserService signUserService;

    /**
     * 根据用户ID加载用户信息
     * Spring Security 在认证时会调用此方法
     *
     * @param userId 用户ID（作为用户名）
     * @return UserDetails 对象
     * @throws UsernameNotFoundException 用户不存在时抛出
     */
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        log.debug("加载用户信息 - userId: {}", userId);

        // 查询用户基本信息
        Long id;
        try {
            id = Long.parseLong(userId);
        } catch (NumberFormatException e) {
            log.error("用户ID格式错误: {}", userId);
            throw new UsernameNotFoundException("用户ID格式错误: " + userId);
        }

        SignUser user = signUserService.findSignUserById(id);
        if (user == null) {
            log.warn("用户不存在 - userId: {}", userId);
            throw new UsernameNotFoundException("用户不存在: " + userId);
        }

        // 检查用户状态 (假设 status 为 0 表示禁用，deleteFlag为1表示已删除)
        if (user.getStatus() != null && StatusFlag.INVALID.getFlag().equals(user.getStatus())) {
            log.warn("用户已被禁用 - userId: {}", userId);
            throw new BusinessException(BadReqExceptionMsg.SIGN_USER_NOT_EXIST.getCode(),
                    BadReqExceptionMsg.SIGN_USER_NOT_EXIST.getMessage());
//            throw new UsernameNotFoundException("用户已被禁用");
        }

        // 获取用户角色列表
        List<RoleResp> roles = signUserService.getRolesByOwner(id);

        // 构建权限列表
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleCode()))
                .collect(Collectors.toList());

        // 构建 UserDetails 对象
        return User.builder()
                .username(userId)
                .password(user.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(true)
                .build();
    }
}
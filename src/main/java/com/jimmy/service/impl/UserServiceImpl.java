package com.jimmy.service.impl;

import com.jimmy.entity.UserInfo;
import com.jimmy.repository.UserInfoRepository;
import com.jimmy.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserInfoRepository userInfoRepository;

    @Override
    public UserInfo findById(Long userId) {
        if (!Objects.isNull(userId)) {
            return userInfoRepository.findUserInfoById(userId);
        }
        return null;
    }
}

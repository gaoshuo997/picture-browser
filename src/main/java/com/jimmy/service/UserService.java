package com.jimmy.service;

import com.jimmy.entity.UserInfo;

public interface UserService {

    UserInfo findById(Long userId);
}

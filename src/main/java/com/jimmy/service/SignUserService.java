package com.jimmy.service;

import com.jimmy.entity.SignUser;
import com.jimmy.req.SignUserReq;

public interface SignUserService {
    SignUser insertSignUser(SignUserReq req);

    SignUser findSignUserById(Long id);
}

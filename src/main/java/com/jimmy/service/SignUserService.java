package com.jimmy.service;

import com.jimmy.common.PaginatedApiResult;
import com.jimmy.entity.SignUser;
import com.jimmy.req.SignUserSave;
import com.jimmy.resp.RoleResp;
import com.jimmy.resp.SignUserResp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface SignUserService {
    SignUser insertSignUser(SignUserSave save);

    SignUser findSignUserById(Long id);

    SignUser checkSignUser(String loginUserName,String password);

    PaginatedApiResult<SignUserResp> list(Integer page, Integer size, String loginName, LocalDateTime startDate, LocalDateTime endDate);

    void deleteById(Long id);

    void update(Long id, SignUserSave save);

    List<Long> getRolesByUserId(Long id);

    List<RoleResp> getRolesByOwner(Long id);

    void assignRoles(Long id, Set<Long> roleIds);

    void setStatus(Long id);
}

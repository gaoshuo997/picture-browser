package com.jimmy.service;

import com.jimmy.common.PaginatedApiResult;
import com.jimmy.req.CoursePacksSave;
import com.jimmy.resp.CoursePacksResp;

public interface CoursePacksService {

    PaginatedApiResult<CoursePacksResp> list(Integer page, Integer size);

    CoursePacksResp fetch(Long id);

    void create(CoursePacksSave save);

    void update(Long id, CoursePacksSave save);
}

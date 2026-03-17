package com.jimmy.service;

import com.jimmy.common.PaginatedApiResult;
import com.jimmy.resp.CourseResp;

public interface CourseService {

    CourseResp fetch(Long courseId);

    PaginatedApiResult<CourseResp> list(Long coursePackId, Integer page, Integer size);
}

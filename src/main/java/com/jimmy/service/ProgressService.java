package com.jimmy.service;

import com.jimmy.common.PaginatedApiResult;
import com.jimmy.resp.LearningProgressResp;

public interface ProgressService {

    PaginatedApiResult<LearningProgressResp> getProgressList(Integer page, Integer size);
}

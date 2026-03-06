package com.jimmy.service;

import com.jimmy.req.LearningProgressReq;
import com.jimmy.resp.LearningProgressResp;

public interface LearningService {

    LearningProgressResp getStart(Long courseId);

    void completeStatement(LearningProgressReq req);
}

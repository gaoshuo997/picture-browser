package com.jimmy.service;

import com.jimmy.req.LearningProgressReq;
import com.jimmy.resp.LearningProgressResp;

public interface LearningService {

    LearningProgressResp getStart(String courseId);

    void completeStatement(LearningProgressReq req);
}

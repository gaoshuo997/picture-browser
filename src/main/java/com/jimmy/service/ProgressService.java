package com.jimmy.service;

import com.jimmy.resp.LearningProgressResp;

import java.util.List;

public interface ProgressService {

    List<LearningProgressResp> getProgressList(Long userId);
}

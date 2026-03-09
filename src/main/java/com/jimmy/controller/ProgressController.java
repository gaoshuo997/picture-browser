package com.jimmy.controller;

import com.jimmy.common.result.Result;
import com.jimmy.resp.LearningProgressResp;
import com.jimmy.service.ProgressService;
import com.jimmy.utils.UserUtils;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/progress")
public class ProgressController {

    @Resource
    private ProgressService progressService;

    @GetMapping("/courses")
    public Result<List<LearningProgressResp>> getProgressList(){
        Long userId = UserUtils.getUserId();
        List<LearningProgressResp> progressList = progressService.getProgressList(userId);
        return Result.success(progressList);
    }
}

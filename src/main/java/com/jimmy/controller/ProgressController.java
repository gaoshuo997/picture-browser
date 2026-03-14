package com.jimmy.controller;

import com.jimmy.common.result.Result;
import com.jimmy.resp.LearningProgressResp;
import com.jimmy.security.SecurityUtils;
import com.jimmy.service.ProgressService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 学习进度接口
 */
@RestController
@RequestMapping("/progress")
public class ProgressController {

    @Resource
    private ProgressService progressService;

    /**
     * 获取学习进度列表
     * @return 学习进度列表
     */
    @GetMapping("/courses")
    public Result<List<LearningProgressResp>> getProgressList(){
        Long userId = SecurityUtils.getCurrentUserId();
        List<LearningProgressResp> progressList = progressService.getProgressList(userId);
        return Result.success(progressList);
    }
}

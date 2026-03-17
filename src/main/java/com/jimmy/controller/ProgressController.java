package com.jimmy.controller;

import com.jimmy.common.PaginatedApiResult;
import com.jimmy.common.result.Result;
import com.jimmy.resp.LearningProgressResp;
import com.jimmy.service.ProgressService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public Result<PaginatedApiResult<LearningProgressResp>> getProgressList(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size){

        page = page >= 1 ? page : 1;
        size = size >= 0 ? size : 10;
        PaginatedApiResult<LearningProgressResp> progressList = progressService.getProgressList(page, size);
        return Result.success(progressList);
    }
}

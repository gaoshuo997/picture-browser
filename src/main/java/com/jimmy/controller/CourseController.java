package com.jimmy.controller;

import com.jimmy.common.PaginatedApiResult;
import com.jimmy.common.result.Result;
import com.jimmy.resp.CourseResp;
import com.jimmy.service.CourseService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 课程接口
 */
@RestController
@RequestMapping("/course")
public class CourseController {

    @Autowired
    private CourseService courseService;

    /**
     * 获取课程详情（课程内部的学习章节）
     * @param courseId 课程ID
     * @return 课程详情
     */
    @GetMapping("/{id}")
    public Result<CourseResp> getDetail(
           @PathVariable("id") Long courseId){
        CourseResp fetch = courseService.fetch(courseId);
        return Result.success(fetch);
    }

    /**
     * 获取课程包列表
     * @return 返回课程包列表
     */
    @GetMapping("/{id}/list")
    public Result<PaginatedApiResult<CourseResp>> getList(
            @PathVariable("id") @NotNull(message = "课程包id不能为空") Long coursePackId,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {

        page = page >= 1 ? page : 1;
        size = size >= 0 ? size : 10;
        PaginatedApiResult<CourseResp> list = courseService.list(coursePackId, page, size);
        return Result.success(list);
    }
}

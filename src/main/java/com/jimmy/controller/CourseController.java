package com.jimmy.controller;

import com.jimmy.common.result.Result;
import com.jimmy.resp.CourseResp;
import com.jimmy.service.CourseService;
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
}

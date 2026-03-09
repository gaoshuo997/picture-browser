package com.jimmy.controller;

import com.jimmy.common.result.Result;
import com.jimmy.resp.CourseResp;
import com.jimmy.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/course")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping("/{id}")
    public Result<CourseResp> getDetail(
           @PathVariable("id") Long courseId){
        CourseResp fetch = courseService.fetch(courseId);
        return Result.success(fetch);
    }
}

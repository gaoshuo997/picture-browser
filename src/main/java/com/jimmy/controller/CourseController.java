package com.jimmy.controller;

import com.jimmy.common.web.ApplicationResponseEntity;
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
    public ApplicationResponseEntity<CourseResp> getDetail(
           @PathVariable("id") String courseId){
        CourseResp fetch = courseService.fetch(courseId);
        ApplicationResponseEntity<CourseResp> result = new ApplicationResponseEntity<>();
        result.setData(fetch);
        return result;
    }
}

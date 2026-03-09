package com.jimmy.controller;

import com.jimmy.common.result.Result;
import com.jimmy.resp.CoursePacksResp;
import com.jimmy.service.CoursePacksService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/course-packs")
public class CoursePacksController {

    @Autowired
    private CoursePacksService coursePacksService;

    @GetMapping("/list")
    public Result<List<CoursePacksResp>> getList() {
        List<CoursePacksResp> list = coursePacksService.list();
        return Result.success(list);
    }

    @GetMapping("/fetch/{id}")
    public Result<CoursePacksResp> fetchCoursePack(
            @PathVariable @NotNull(message = "id不能为空") Long id) {
        CoursePacksResp coursePacksResp = coursePacksService.fetch(id);
        return Result.success(coursePacksResp);
    }
}

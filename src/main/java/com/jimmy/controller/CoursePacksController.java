package com.jimmy.controller;

import com.jimmy.common.web.ApplicationResponseEntity;
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
    public ApplicationResponseEntity<List<CoursePacksResp>> getList() {
        List<CoursePacksResp> list = coursePacksService.list();
        ApplicationResponseEntity<List<CoursePacksResp>> result = new ApplicationResponseEntity<>();
        result.setData(list);
        return result;
    }

    @GetMapping("/fetch/{id}")
    public ApplicationResponseEntity<CoursePacksResp> fetchCoursePack(
            @PathVariable @NotNull(message = "id不能为空") String id) {
        CoursePacksResp coursePacksResp = coursePacksService.fetch(id);
        ApplicationResponseEntity<CoursePacksResp> result = new ApplicationResponseEntity<>();
        result.setData(coursePacksResp);
        return result;
    }
}

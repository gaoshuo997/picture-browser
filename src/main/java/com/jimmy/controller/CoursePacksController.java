package com.jimmy.controller;

import com.jimmy.common.result.Result;
import com.jimmy.req.CoursePacksSave;
import com.jimmy.resp.CoursePacksResp;
import com.jimmy.service.CoursePacksService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课程包管理接口
 */
@RestController
@RequestMapping("/course-packs")
public class CoursePacksController {

    @Autowired
    private CoursePacksService coursePacksService;

    /**
     * 获取课程包列表
     * @return 返回课程包列表
     */
    @GetMapping("/list")
    public Result<List<CoursePacksResp>> getList() {
        List<CoursePacksResp> list = coursePacksService.list();
        return Result.success(list);
    }

    /**
     * 获取课程包详细信息（课程列表）
     * @param id 课程包ID
     * @return 返回课程列表
     */
    @GetMapping("/fetch/{id}")
    public Result<CoursePacksResp> fetchCoursePack(
            @PathVariable @NotNull(message = "id不能为空") Long id) {
        CoursePacksResp coursePacksResp = coursePacksService.fetch(id);
        return Result.success(coursePacksResp);
    }

    /**
     * 新增课程包
     * @param save 课程包新增参数
     */
    @PostMapping("/create")
    public Result<Void> create(@RequestBody CoursePacksSave save) {
        coursePacksService.create(save);
        return Result.success();
    }

    /**
     * 更新课程包
     */
    @PutMapping("/update/{id}")
    public Result<Void> update(
            @PathVariable @NotNull(message = "id不能为空") Long id,
            @RequestBody CoursePacksSave save) {
        coursePacksService.update(id, save);
        return Result.success();
    }
}

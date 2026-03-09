package com.jimmy.controller;

import com.jimmy.common.result.Result;
import com.jimmy.req.LearningProgressReq;
import com.jimmy.resp.LearningProgressResp;
import com.jimmy.service.LearningService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/learning")
public class LearningController {

    @Resource
    private LearningService learningService;

    @GetMapping("/start/{id}")
    public Result<LearningProgressResp> getStart(
            @PathVariable("id") @NotNull(message = "课程id不能为空")Long courseId){
        LearningProgressResp start = learningService.getStart(courseId);
        return Result.success(start);
    }

    @PostMapping("/statement/complete")
    public Result<?> completeStatement(@Valid @RequestBody LearningProgressReq req){
        learningService.completeStatement(req);
        return Result.success();
    }
}

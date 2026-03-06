package com.jimmy.controller;

import com.jimmy.common.web.ApplicationResponseEntity;
import com.jimmy.req.LearningProgressReq;
import com.jimmy.resp.LearningProgressResp;
import com.jimmy.service.LearningService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/learning")
public class LearningController {

    @Resource
    private LearningService learningService;

    @GetMapping("/start/{id}")
    public ApplicationResponseEntity<LearningProgressResp> getStart(
            @PathVariable("id") @NotNull(message = "课程id不能为空")Long courseId){
        LearningProgressResp start = learningService.getStart(courseId);
        ApplicationResponseEntity<LearningProgressResp> result = new ApplicationResponseEntity<>();
        result.setData(start);
        return result;
    }

    @PostMapping("/statement/complete")
    public ApplicationResponseEntity<Map<String, String>> completeStatement(@Valid @RequestBody LearningProgressReq req){
        learningService.completeStatement(req);
        ApplicationResponseEntity<Map<String, String>> result = new ApplicationResponseEntity<>();
        Map<String, String> data = Map.of("message", "ok");
        result.setData(data);
        return result;
    }
}

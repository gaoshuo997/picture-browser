package com.jimmy.controller;

import com.jimmy.common.result.Result;
import com.jimmy.resp.StudyDurationResp;
import com.jimmy.service.StatisticService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/statistics")
public class StatisticController {

    @Resource
    private StatisticService statisticService;

    @GetMapping("/duration")
    public Result<List<StudyDurationResp>> getDuration(){
        List<StudyDurationResp> duration = statisticService.getDuration();
        return Result.success(duration);
    }

    @GetMapping("/duration/today")
    public Result<StudyDurationResp> getDurationByDay(){
        StudyDurationResp durationByDay = statisticService.getDurationByDay();
        return Result.success(durationByDay);
    }

    // 获取一周的学习时长
    @GetMapping("/duration/week")
    public Result<StudyDurationResp> getDurationByWeek(){
        StudyDurationResp durationByWeek = statisticService.getDurationByWeek();
        return Result.success(durationByWeek);
    }

    // 获取一月的学习时长
    @GetMapping("/duration/month")
    public Result<StudyDurationResp> getDurationByMonth(){
        StudyDurationResp durationByMonth = statisticService.getDurationByMonth();
        return Result.success(durationByMonth);
    }
}

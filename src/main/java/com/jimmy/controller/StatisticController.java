package com.jimmy.controller;

import com.jimmy.common.result.Result;
import com.jimmy.resp.StudyDurationResp;
import com.jimmy.service.StatisticService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 统计信息接口
 */
@RestController
@RequestMapping("/statistics")
public class StatisticController {

    @Resource
    private StatisticService statisticService;

    // 获取所有学习时长
    @GetMapping("/duration")
    public Result<List<StudyDurationResp>> getDuration(){
        List<StudyDurationResp> duration = statisticService.getDuration();
        return Result.success(duration);
    }

    // 获取当天的学习时长
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

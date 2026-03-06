package com.jimmy.controller;

import com.jimmy.common.web.ApplicationResponseEntity;
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
    public ApplicationResponseEntity<List<StudyDurationResp>> getDuration(){
        List<StudyDurationResp> duration = statisticService.getDuration();
        ApplicationResponseEntity<List<StudyDurationResp>> result = new ApplicationResponseEntity<>();
        result.setData(duration);
        return result;
    }

    @GetMapping("/duration/today")
    public ApplicationResponseEntity<StudyDurationResp> getDurationByDay(){
        StudyDurationResp durationByDay = statisticService.getDurationByDay();
        ApplicationResponseEntity<StudyDurationResp> result = new ApplicationResponseEntity<>();
        result.setData(durationByDay);
        return result;
    }

    // 获取一周的学习时长
    @GetMapping("/duration/week")
    public ApplicationResponseEntity<StudyDurationResp> getDurationByWeek(){
        StudyDurationResp durationByDay = statisticService.getDurationByWeek();
        ApplicationResponseEntity<StudyDurationResp> result = new ApplicationResponseEntity<>();
        result.setData(durationByDay);
        return result;
    }

    // 获取一月的学习时长
    @GetMapping("/duration/month")
    public ApplicationResponseEntity<StudyDurationResp> getDurationByMonth(){
        StudyDurationResp durationByDay = statisticService.getDurationByMonth();
        ApplicationResponseEntity<StudyDurationResp> result = new ApplicationResponseEntity<>();
        result.setData(durationByDay);
        return result;
    }
}

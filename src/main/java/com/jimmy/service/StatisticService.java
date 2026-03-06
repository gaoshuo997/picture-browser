package com.jimmy.service;

import com.jimmy.resp.StudyDurationResp;

import java.util.List;

public interface StatisticService {

    List<StudyDurationResp> getDuration();

    StudyDurationResp getDurationByDay();

    StudyDurationResp getDurationByWeek();

    StudyDurationResp getDurationByMonth();
}

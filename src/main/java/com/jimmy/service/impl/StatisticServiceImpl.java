package com.jimmy.service.impl;

import com.jimmy.entity.UserLearnRecord;
import com.jimmy.repository.UserLearnRecordRepository;
import com.jimmy.resp.StudyDurationResp;
import com.jimmy.service.StatisticService;
import com.jimmy.utils.UserUtils;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticServiceImpl implements StatisticService {

    @Resource
    private UserLearnRecordRepository userLearnRecordRepository;

    @Override
    public List<StudyDurationResp> getDuration() {
        List<UserLearnRecord> allRecord = userLearnRecordRepository.findByUserId(UserUtils.getUserId());
        Map<LocalDate, List<UserLearnRecord>> groupByDate = allRecord.stream()
                .collect(Collectors.groupingBy(UserLearnRecord::getDay));

        return getStudyDurationResps(groupByDate);
    }



    @Override
    public StudyDurationResp getDurationByDay() {
        UserLearnRecord record = userLearnRecordRepository
                .findByDayAndUserId(LocalDate.now(), UserUtils.getUserId());
        StudyDurationResp resp = new StudyDurationResp();
        resp.setDuration(record.getDuration());
        resp.setDate(record.getDay().format(DateTimeFormatter.ISO_DATE));
        resp.setUserId(UserUtils.getUserId());
        return resp;
    }

    @Override
    public StudyDurationResp getDurationByWeek() {
        LocalDate today = LocalDate.now();
        // 本自然周的周一
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        StudyDurationResp resp = new StudyDurationResp();
        List<UserLearnRecord> betweenWeek = userLearnRecordRepository.findByUserIdAndDayBetween(UserUtils.getUserId(),
                startOfWeek, today);
        Integer totalDuration = betweenWeek.stream().mapToInt(UserLearnRecord::getDuration).sum();
        resp.setDuration(totalDuration);
        return resp;
    }

    @Override
    public StudyDurationResp getDurationByMonth() {
        LocalDate today = LocalDate.now();
        // 本自然月的第一天
        LocalDate firstDayOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());
        List<UserLearnRecord> betweenMonth = userLearnRecordRepository.findByUserIdAndDayBetween(UserUtils.getUserId(),
                firstDayOfMonth, today);
        Integer totalDuration = betweenMonth.stream().mapToInt(UserLearnRecord::getDuration).sum();
        StudyDurationResp resp = new StudyDurationResp();
        resp.setDuration(totalDuration);
        return resp;
    }


    @NotNull
    private List<StudyDurationResp> getStudyDurationResps(Map<LocalDate, List<UserLearnRecord>> groupByDate) {
        List<StudyDurationResp> respList = new ArrayList<>(groupByDate.size());
        groupByDate.forEach((date, records) -> {
            Integer totalDuration = records.stream().mapToInt(UserLearnRecord::getDuration).sum();
            StudyDurationResp resp = new StudyDurationResp();
            resp.setDuration(totalDuration);
            resp.setDate(date.format(DateTimeFormatter.ISO_DATE));
            resp.setUserId(UserUtils.getUserId());
            respList.add(resp);
        });
        return respList;
    }
}

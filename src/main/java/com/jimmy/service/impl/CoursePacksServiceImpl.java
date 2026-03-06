package com.jimmy.service.impl;

import com.jimmy.entity.CourseHistory;
import com.jimmy.entity.CoursePacks;
import com.jimmy.entity.Courses;
import com.jimmy.entity.Statements;
import com.jimmy.repository.*;
import com.jimmy.resp.CoursePacksResp;
import com.jimmy.resp.CourseResp;
import com.jimmy.service.CoursePacksService;
import com.jimmy.utils.DateUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class CoursePacksServiceImpl implements CoursePacksService {

    @Resource
    private CoursePacksRepository coursePacksRepository;
    @Resource
    private CoursesRepository coursesRepository;
    @Resource
    private CourseHistoryRepository courseHistoryRepository;
    @Resource
    private StatementsRepository statementsRepository;

    @Override
    public List<CoursePacksResp> list() {
        List<CoursePacks> all = coursePacksRepository.findAll();
        List<CoursePacksResp> result = new ArrayList<>();
        for (CoursePacks coursePacks : all) {
            CoursePacksResp resp = new CoursePacksResp();
            BeanUtils.copyProperties(coursePacks,resp);
            resp.setCourseCount(coursesRepository.countByCoursePackId(coursePacks.getId()));
            result.add(resp);
        }
        return result;
    }

    @Override
    public CoursePacksResp fetch(Long id) {
        Optional<CoursePacks> byId = coursePacksRepository.findById(id);
        if (byId.isPresent()) {
            CoursePacks coursePacks = byId.get();
            CoursePacksResp resp = new CoursePacksResp();
            BeanUtils.copyProperties(coursePacks, resp);
            resp.setCreatedAt(DateUtils.format(coursePacks.getCreatedAt(),DateUtils.DATE_FORMAT));

            // 根据课程包获取课程表
            List<Courses> coursesByCoursePackId = coursesRepository.findByCoursePackIdOrderByOrderAsc(coursePacks.getId());

            // 根据课程包获取用户历史学习记录
            List<CourseHistory> historyByCoursePackId = courseHistoryRepository.findByCoursePackId(coursePacks.getId());
            // 根据课程包获取句子
            List<Statements> statementsList = statementsRepository.findByCourseIdIn(coursesByCoursePackId.stream()
                    .map(Courses::getId).toList());

            Map<Long, List<Statements>> statementGroup = statementsList.stream()
                    .collect(Collectors.groupingBy(Statements::getCourseId));

            resp.setCourses(coursesByCoursePackId.stream().map(course -> {
                CourseResp courseResp = new CourseResp();
                BeanUtils.copyProperties(course, courseResp);
                courseResp.setCoursePackId(coursePacks.getId());
                Optional<CourseHistory> first = historyByCoursePackId.stream().filter(history -> history.getCourseId().equals(course.getId())).findFirst();
                courseResp.setCompletionCount(first.map(CourseHistory::getCompletionCount).orElse(0));
                courseResp.setStatementCount(statementGroup.get(course.getId()).size());
                return courseResp;
            }).toList());
            return resp;
        }
        return null;
    }
}

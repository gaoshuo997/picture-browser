package com.jimmy.service.impl;

import com.jimmy.common.result.BusinessException;
import com.jimmy.constant.PredicateFieldName;
import com.jimmy.entity.*;
import com.jimmy.repository.*;
import com.jimmy.req.CoursePacksSave;
import com.jimmy.resp.CoursePacksResp;
import com.jimmy.resp.CourseResp;
import com.jimmy.service.CoursePacksService;
import com.jimmy.utils.DateUtils;
import jakarta.annotation.Resource;
import com.jimmy.security.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    @Resource
    private MediaRepository mediaRepository;

    @Override
    public List<CoursePacksResp> list() {
        Sort sortByCreateAt = Sort.by(Sort.Direction.ASC, PredicateFieldName.CREATED_AT.getName());
        List<CoursePacks> all = coursePacksRepository.findAll(sortByCreateAt);

        List<Media> allMediaById = mediaRepository.findAllById(all.stream().map(CoursePacks::getCover).toList());
        List<CoursePacksResp> result = new ArrayList<>();
        for (CoursePacks coursePacks : all) {
            CoursePacksResp resp = new CoursePacksResp();
            BeanUtils.copyProperties(coursePacks,resp);
            resp.setCourseCount(coursesRepository.countByCoursePackId(coursePacks.getId()));

            Optional<Media> first = allMediaById.stream()
                    .filter(media -> media.getId().equals(coursePacks.getCover())).findFirst();
            if (first.isPresent()){
                resp.setCoverUrl(first.get().getUrl());
                resp.setCoverFileName(first.get().getFileName());
            }
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

            mediaRepository.findById(coursePacks.getCover()).ifPresent(media -> {
                resp.setCoverUrl(media.getUrl());
                resp.setCoverFileName(media.getFileName());
            });
            return resp;
        }
        return null;
    }

    @Override
    public void create(CoursePacksSave save) {
        LocalDateTime now = LocalDateTime.now();
        CoursePacks coursePacks = new CoursePacks();
        BeanUtils.copyProperties(save, coursePacks);
        coursePacks.setCreatedAt(now);
        coursePacks.setUpdatedAt(now);
        coursePacks.setCreatorId(SecurityUtils.getCurrentUserId());
        coursePacksRepository.save(coursePacks);

    }

    @Override
    public void update(Long id, CoursePacksSave save) {
        CoursePacks coursePacks = checkCoursePacksExists(id);
        BeanUtils.copyProperties(save, coursePacks);
        coursePacks.setUpdatedAt(LocalDateTime.now());
        coursePacksRepository.save(coursePacks);
    }

    private CoursePacks checkCoursePacksExists(Long id) {
        Optional<CoursePacks> byId = coursePacksRepository.findById(id);
        if (byId.isEmpty()) {
            throw new BusinessException("课程包不存在");
        }
        return byId.get();
    }
}

package com.jimmy.service.impl;

import com.jimmy.common.PaginatedApiResult;
import com.jimmy.constant.PredicateFieldName;
import com.jimmy.entity.Courses;
import com.jimmy.entity.UserCourseProgress;
import com.jimmy.entity.dto.CourseStateCountDTO;
import com.jimmy.repository.CoursesRepository;
import com.jimmy.repository.UserCourseProgressRepository;
import com.jimmy.resp.LearningProgressResp;
import com.jimmy.security.SecurityUtils;
import com.jimmy.service.ProgressService;
import com.jimmy.utils.DateUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProgressServiceImpl implements ProgressService {

    @Resource
    private UserCourseProgressRepository progressRepository;
    @Resource
    private CoursesRepository coursesRepository;

    @Override
    public PaginatedApiResult<LearningProgressResp> getProgressList(Integer page, Integer size) {

        Pageable pageable = PageRequest.of(page - 1, size,
                Sort.by(Sort.Direction.ASC, PredicateFieldName.CREATED_AT.getName()));
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Page<UserCourseProgress> pageList = progressRepository.findByUserId(currentUserId,pageable);
        if (pageList.isEmpty()){
            return new PaginatedApiResult<>(pageable.getPageNumber(),pageable.getPageSize(),
                    0,0L, new ArrayList<>(),0);
        }

        List<LearningProgressResp> respList = new ArrayList<>(pageList.getSize());
        Set<Long> courseIdByProgress = pageList.stream().map(UserCourseProgress::getCourseId)
                .collect(Collectors.toSet());
        List<Courses> allCourseById = coursesRepository.findAllById(courseIdByProgress);

        List<CourseStateCountDTO> countStatementByCourse = coursesRepository
                .countStatementPreCourse(allCourseById.stream().map(Courses::getId).toList());

        for (UserCourseProgress userCourseProgress : pageList) {
            LearningProgressResp resp = new LearningProgressResp();
            BeanUtils.copyProperties(userCourseProgress, resp);
            allCourseById.stream().filter(course -> course.getId()
                            .equals(userCourseProgress.getCourseId()))
                    .findFirst().ifPresent(course -> resp.setCourseTitle(course.getTitle()));
            resp.setLastStudyAt(DateUtils.format(userCourseProgress.getUpdatedAt(), DateUtils.DATETIME_FORMAT));
            resp.setCountStatementByCourse(countStatementByCourse.stream()
                    .filter(c -> c.getCourseId().equals(resp.getCourseId()))
                    .findFirst().orElse(new CourseStateCountDTO()).getStatementCount());
            respList.add(resp);
        }
        return new PaginatedApiResult<>(pageable.getPageNumber(),pageable.getPageSize(),
                respList.size(),pageList.getTotalElements(),
                respList,pageList.getTotalPages());

    }
}

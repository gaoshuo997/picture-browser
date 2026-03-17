package com.jimmy.service.impl;

import com.jimmy.common.PaginatedApiResult;
import com.jimmy.common.result.BusinessException;
import com.jimmy.constant.PredicateFieldName;
import com.jimmy.entity.*;
import com.jimmy.repository.*;
import com.jimmy.req.CoursePacksSave;
import com.jimmy.resp.CoursePacksResp;
import com.jimmy.service.CoursePacksService;
import com.jimmy.utils.DateUtils;
import jakarta.annotation.Resource;
import com.jimmy.security.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class CoursePacksServiceImpl implements CoursePacksService {

    @Resource
    private CoursePacksRepository coursePacksRepository;
    @Resource
    private CoursesRepository coursesRepository;
    @Resource
    private MediaRepository mediaRepository;

    @Override
    public PaginatedApiResult<CoursePacksResp> list(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size,
                Sort.by(Sort.Direction.ASC, PredicateFieldName.CREATED_AT.getName()));
        Page<CoursePacks> pageList = coursePacksRepository.findAll(pageable);

        if (pageList.isEmpty()) {
            return new PaginatedApiResult<>(page, size, 0, 0L, Collections.emptyList(), 0);
        }

        List<Media> allMediaById = mediaRepository.findAllById(pageList.stream().map(CoursePacks::getCover).toList());
        List<CoursePacksResp> respList = new ArrayList<>();
        for (CoursePacks coursePacks : pageList) {
            CoursePacksResp resp = new CoursePacksResp();
            BeanUtils.copyProperties(coursePacks,resp);
            resp.setCourseCount(coursesRepository.countByCoursePackId(coursePacks.getId()));

            Optional<Media> first = allMediaById.stream()
                    .filter(media -> media.getId().equals(coursePacks.getCover())).findFirst();
            if (first.isPresent()){
                resp.setCoverUrl(first.get().getUrl());
                resp.setCoverFileName(first.get().getFileName());
            }
            respList.add(resp);
        }
        return new PaginatedApiResult<>(pageable.getPageNumber(),pageable.getPageSize(),
                respList.size(),pageList.getTotalElements(),
                respList,pageList.getTotalPages());
    }

    @Override
    public CoursePacksResp fetch(Long id) {
        Optional<CoursePacks> byId = coursePacksRepository.findById(id);
        if (byId.isPresent()) {
            CoursePacks coursePacks = byId.get();
            CoursePacksResp resp = new CoursePacksResp();
            BeanUtils.copyProperties(coursePacks, resp);
            resp.setCreatedAt(DateUtils.format(coursePacks.getCreatedAt(),DateUtils.DATE_FORMAT));

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

package com.jimmy.service.impl;

import com.jimmy.common.PaginatedApiResult;
import com.jimmy.constant.PredicateFieldName;
import com.jimmy.entity.CourseHistory;
import com.jimmy.entity.Courses;
import com.jimmy.entity.Statements;
import com.jimmy.repository.CourseHistoryRepository;
import com.jimmy.repository.CoursesRepository;
import com.jimmy.repository.StatementsRepository;
import com.jimmy.resp.CourseResp;
import com.jimmy.resp.StatementResp;
import com.jimmy.service.CourseService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class CourseServiceImpl implements CourseService {

    @Resource
    private CoursesRepository coursesRepository;
    @Resource
    private StatementsRepository statementsRepository;
    @Resource
    private CourseHistoryRepository courseHistoryRepository;

    @Override
    public CourseResp fetch(Long courseId) {
        Optional<Courses> byId = coursesRepository.findById(courseId);
        if (byId.isEmpty()){
            throw new RuntimeException("课程不存在");
        }
        List<Statements> byCourseId = statementsRepository.findByCourseId(courseId);
        CourseResp resp = new CourseResp();
        BeanUtils.copyProperties(byId.get(),resp);

        List<StatementResp> statementRespList = new ArrayList<>(byCourseId.size());
        for (Statements statement : byCourseId){
            StatementResp statementResp = new StatementResp();
            BeanUtils.copyProperties(statement, statementResp);
            statementRespList.add(statementResp);
        }
        statementRespList.sort(Comparator.comparing(StatementResp::getOrder));
        resp.setStatements(statementRespList);
        return resp;
    }

    @Override
    public PaginatedApiResult<CourseResp> list(Long coursePackId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page-1,size, Sort.by(Sort.Direction.ASC,
                PredicateFieldName.ORDER.getName()));
        Page<Courses> pageList = coursesRepository.findByCoursePackId(coursePackId, pageable);
        if (pageList.isEmpty()){
            return new PaginatedApiResult<>(pageable.getPageNumber(),pageable.getPageSize(),
                    0,0L,null,0);
        }
        List<Long> courseIdList = pageList.stream().map(Courses::getId).toList();
        List<CourseHistory> historyByCourseIdIn = courseHistoryRepository.findByCourseIdIn(courseIdList);

        // 获取查询到的所有课程章节
        List<Statements> byCourseId = statementsRepository.findByCourseIdIn(courseIdList);

        // 根据课程对章节进行分组
        Map<Long, List<Statements>> statementGroup = byCourseId.stream()
                .collect(Collectors.groupingBy(Statements::getCourseId));

        List<CourseResp> respList = new ArrayList<>(pageList.getSize());
        for (Courses course : pageList.getContent()) {
            CourseResp resp = new CourseResp();
            BeanUtils.copyProperties(course, resp);

            // 拼装章节返回信息
            List<Statements> orDefault = statementGroup.getOrDefault(course.getId(), new ArrayList<>());
            if (!orDefault.isEmpty()){
                List<StatementResp> statementRespList = new ArrayList<>(byCourseId.size());
                for (Statements statement : orDefault){
                    StatementResp statementResp = new StatementResp();
                    BeanUtils.copyProperties(statement, statementResp);
                    statementRespList.add(statementResp);
                }
                statementRespList.sort(Comparator.comparing(StatementResp::getOrder));
                resp.setStatements(statementRespList);
            }

            // 返回课程学习进度信息
            resp.setCompletionCount(historyByCourseIdIn.stream().filter(history -> history.getCourseId().equals(course.getId()))
                    .findFirst().map(CourseHistory::getCompletionCount).orElse(0));
            resp.setStatementCount(orDefault.size());

            respList.add(resp);
        }
        return new PaginatedApiResult<>(pageable.getPageNumber(),pageable.getPageSize(),
                respList.size(),pageList.getTotalElements(),
                respList,pageList.getTotalPages());
    }
}

package com.jimmy.service.impl;

import com.jimmy.entity.Courses;
import com.jimmy.entity.Statements;
import com.jimmy.repository.CoursesRepository;
import com.jimmy.repository.StatementsRepository;
import com.jimmy.resp.CourseResp;
import com.jimmy.resp.StatementResp;
import com.jimmy.service.CourseService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(rollbackFor = Exception.class)
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CoursesRepository coursesRepository;
    @Autowired
    private StatementsRepository statementsRepository;

    @Override
    public CourseResp fetch(String courseId) {
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
        statementRespList.sort((s1, s2) -> s1.getOrder().compareTo(s2.getOrder()));
        resp.setStatements(statementRespList);
        return resp;
    }
}

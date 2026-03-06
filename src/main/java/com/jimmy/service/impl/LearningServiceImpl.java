package com.jimmy.service.impl;

import com.jimmy.entity.*;
import com.jimmy.entity.enums.MediaType;
import com.jimmy.repository.*;
import com.jimmy.req.LearningProgressReq;
import com.jimmy.req.MediaReq;
import com.jimmy.resp.LearningProgressResp;
import com.jimmy.service.LearningService;
import com.jimmy.utils.DateUtils;
import com.jimmy.utils.UserUtils;
import jakarta.annotation.Resource;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(rollbackFor = Exception.class)
public class LearningServiceImpl implements LearningService {

    @Resource
    private UserCourseProgressRepository userCourseProgressRepository;
    @Resource
    private CourseHistoryRepository courseHistoryRepository;
    @Resource
    private StatementsRepository statementsRepository;
    @Resource
    private CoursesRepository coursesRepository;
    @Resource
    private UserLearnRecordRepository userLearnRecordRepository;

    @Override
    public LearningProgressResp getStart(String courseId) {
        UserCourseProgress first = userCourseProgressRepository
                .findFirstByCourseIdAndUserId(courseId, UserUtils.getUserId());

        if(first == null){
            return null;
        }
        Integer statementIndex = first.getStatementIndex();
        LearningRecord record = new LearningRecord(first.getUserId(),first.getCourseId(),first.getCoursePackId(),
                statementIndex,0);

        Specification<Statements> statementsSpecification = buildSpecification(record);
        Sort sort = Sort.by(Sort.Direction.ASC, "order");
        List<Statements> lessOrder = statementsRepository.findAll(statementsSpecification, sort);

        LearningProgressResp resp = new LearningProgressResp();
        BeanUtils.copyProperties(first,resp);
        resp.setCompletedStatement(lessOrder.stream().map(Statements::getId).toList());
        resp.setLastStudyAt(DateUtils.format(first.getUpdatedAt(),DateUtils.DATETIME_FORMAT));
        return resp;

    }

    @Override
    public void completeStatement(LearningProgressReq req) {
        Optional<Statements> byId = statementsRepository.findById(req.getStatementId());
        if (byId.isEmpty()){
            throw new RuntimeException("当前学习的句子ID不合法");
        }
        Statements statements = byId.get();
        Optional<Courses> coursesOptional = coursesRepository.findById(statements.getCourseId());
        if (coursesOptional.isEmpty()){
            throw new RuntimeException("课程信息异常");
        }
        // 保存历史记录
        LearningRecord record = new LearningRecord(UserUtils.getUserId(), statements.getCourseId(),
                coursesOptional.get().getCoursePackId(), statements.getOrder(),
                req.getDuration());
        saveCourseHistory(record);

        // 保存学习进度
        saveUserProgress(record);

        // 保存学习时间


    }

    /**
     * 保存或更新课程记录
     * @param record
     */
    private void saveCourseHistory(LearningRecord record){
        CourseHistory courseHistory = courseHistoryRepository
                .findFirstByCourseIdAndUserIdAndCoursePackId(record.courseId(),
                        UserUtils.getUserId(), record.coursePackId());
        Date now = new Date();
        if (courseHistory == null){
            courseHistory = new CourseHistory();
            courseHistory.setId(java.util.UUID.randomUUID().toString());
            courseHistory.setCourseId(record.courseId());
            courseHistory.setUserId(UserUtils.getUserId());
            courseHistory.setCoursePackId(record.coursePackId());
            courseHistory.setCompletionCount(record.order());
            courseHistory.setCreatedAt(now);
            courseHistory.setUpdatedAt(now);
        }else {
            courseHistory.setCompletionCount(Math.max(courseHistory.getCompletionCount(), record.order()));
            courseHistory.setUpdatedAt(now);
        }
        courseHistoryRepository.save(courseHistory);
    }

    /**
     * 保存用户学习进度
     * @param record
     */
    private void saveUserProgress(LearningRecord record){
        UserCourseProgress userProgress = userCourseProgressRepository
                .findFirstByCoursePackIdAndUserIdAndCourseId(record.coursePackId(),
                        record.userId(), record.courseId());
        Date now = new Date();
        Integer count;

        if (userProgress == null){
            userProgress = new UserCourseProgress();
            userProgress.setId(java.util.UUID.randomUUID().toString());
            userProgress.setUserId(record.userId());
            userProgress.setCourseId(record.courseId());
            userProgress.setCoursePackId(record.coursePackId());
            userProgress.setStatementIndex(record.order() - 1);
            userProgress.setCreatedAt(now);
            userProgress.setUpdatedAt(now);
            count = record.order();
        }else {
            count = record.order() - userProgress.getStatementIndex();
            userProgress.setStatementIndex(Math.max(userProgress.getStatementIndex() - 1, record.order()));
            userProgress.setUpdatedAt(now);
        }
        userCourseProgressRepository.save(userProgress);

        UserLearnRecord userLearnRecord = userLearnRecordRepository.findByDayAndUserId(LocalDate.now(),
                UserUtils.getUserId());
        if (userLearnRecord == null){
            userLearnRecord = new UserLearnRecord();
            userLearnRecord.setId(java.util.UUID.randomUUID().toString());
            userLearnRecord.setUserId(UserUtils.getUserId());
            userLearnRecord.setDay(LocalDate.now());
            userLearnRecord.setDuration(record.duration());
            userLearnRecord.setUpdatedAt(now);
            userLearnRecord.setCreatedAt(now);
            userLearnRecord.setCount(count);
        }else {
            userLearnRecord.setCount(userLearnRecord.getCount() + count);
            userLearnRecord.setDuration(userLearnRecord.getDuration() + record.duration());
        }
        userLearnRecordRepository.save(userLearnRecord);
    }

    private Specification<Statements> buildSpecification(LearningRecord record){
        return (root, query, cb) -> {
            // 存储查询条件的集合
            List<Predicate> predicates = new ArrayList<>();
            if (record.courseId() != null && !record.courseId().isEmpty()){
                predicates.add(cb.equal(root.get("courseId"), record.courseId()));
            }
            if (record.order() != null){
                predicates.add(cb.lessThanOrEqualTo(root.get("order"), record.order()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

    }

    private record LearningRecord(Long userId,String courseId,
                                  String coursePackId,
                                  Integer order,
                                  Integer duration){}
}

package com.jimmy.service.impl;

import com.jimmy.entity.*;
import com.jimmy.repository.*;
import com.jimmy.req.LearningProgressReq;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    public LearningProgressResp getStart(Long courseId) {
        UserCourseProgress first = userCourseProgressRepository
                .findFirstByCourseIdAndUserId(courseId, UserUtils.getUserId());

        if(first == null){
            LearningProgressResp resp = new LearningProgressResp();
            resp.setUserId(UserUtils.getUserId());
            resp.setCourseId(courseId);
            resp.setStatementIndex(0);
            resp.setCompletedStatement(List.of());
            return resp;
        }
        Integer statementIndex = first.getStatementIndex();
        LearningRecord record = new LearningRecord(first.getUserId(),first.getCourseId(),first.getCoursePackId(),
                statementIndex,0,0);

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
                req.getDuration(),req.getCount());
        saveCourseHistory(record);

        // 保存学习进度
        saveUserProgress(record);

        // 保存学习时间


    }

    /**
     * 保存或更新课程记录
     * @param record 保存参数
     */
    private void saveCourseHistory(LearningRecord record){
        CourseHistory courseHistory = courseHistoryRepository
                .findFirstByCourseIdAndUserIdAndCoursePackId(record.courseId(),
                        UserUtils.getUserId(), record.coursePackId());
        LocalDateTime now = LocalDateTime.now();
        if (courseHistory == null){
            courseHistory = new CourseHistory();
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
     * @param record 参数
     */
    private void saveUserProgress(LearningRecord record){
        UserCourseProgress userProgress = userCourseProgressRepository
                .findFirstByCoursePackIdAndUserIdAndCourseId(record.coursePackId(),
                        record.userId(), record.courseId());

        LocalDateTime now = LocalDateTime.now();
        if (userProgress == null){
            userProgress = new UserCourseProgress();
            userProgress.setUserId(record.userId());
            userProgress.setCourseId(record.courseId());
            userProgress.setCoursePackId(record.coursePackId());
            userProgress.setStatementIndex(record.order());
            userProgress.setCreatedAt(now);
            userProgress.setUpdatedAt(now);
        }else {
            userProgress.setStatementIndex(Math.max(userProgress.getStatementIndex(), record.order()));
            userProgress.setUpdatedAt(now);
        }
        userCourseProgressRepository.save(userProgress);

        UserLearnRecord userLearnRecord = userLearnRecordRepository.findByDayAndUserId(LocalDate.now(),
                UserUtils.getUserId());
        if (userLearnRecord == null){
            userLearnRecord = new UserLearnRecord();
            userLearnRecord.setUserId(UserUtils.getUserId());
            userLearnRecord.setDay(LocalDate.now());
            userLearnRecord.setDuration(record.duration());
            userLearnRecord.setUpdatedAt(now);
            userLearnRecord.setCreatedAt(now);
            userLearnRecord.setCount(record.count());
        }else {
            userLearnRecord.setCount(userLearnRecord.getCount() + record.count());
            userLearnRecord.setDuration(userLearnRecord.getDuration() + record.duration());
        }
        userLearnRecordRepository.save(userLearnRecord);
    }

    private Specification<Statements> buildSpecification(LearningRecord record){
        return (root, query, cb) -> {
            // 存储查询条件的集合
            List<Predicate> predicates = new ArrayList<>();
            if (record.courseId() != null){
                predicates.add(cb.equal(root.get("courseId"), record.courseId()));
            }
            if (record.order() != null){
                predicates.add(cb.lessThanOrEqualTo(root.get("order"), record.order()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

    }

    private record LearningRecord(Long userId,Long courseId,
                                  Long coursePackId,
                                  Integer order,
                                  Integer duration,
                                  Integer count){}
}

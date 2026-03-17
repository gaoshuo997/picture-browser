package com.jimmy.repository;

import com.jimmy.entity.CourseHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface CourseHistoryRepository extends JpaRepository<CourseHistory, Long> {

    CourseHistory findFirstByCourseIdAndUserIdAndCoursePackId(Long courseId, Long userId, Long coursePackId);

    List<CourseHistory> findByCourseIdIn(Collection<Long> courseIds);
}

package com.jimmy.repository;

import com.jimmy.entity.CourseHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseHistoryRepository extends JpaRepository<CourseHistory, String> {

    List<CourseHistory> findByCoursePackId(String coursePakId);

    CourseHistory findFirstByCourseIdAndUserIdAndCoursePackId(String courseId, Long userId, String coursePackId);
}

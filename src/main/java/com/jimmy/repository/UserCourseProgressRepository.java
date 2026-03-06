package com.jimmy.repository;

import com.jimmy.entity.UserCourseProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCourseProgressRepository extends JpaRepository<UserCourseProgress, String> {
    UserCourseProgress findFirstByCourseIdAndUserId(String courseId, Long userId);

    UserCourseProgress findFirstByCoursePackIdAndUserIdAndCourseId(String coursePackId, Long userId, String courseId);
}

package com.jimmy.repository;

import com.jimmy.entity.UserCourseProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCourseProgressRepository extends JpaRepository<UserCourseProgress, Long> {
    UserCourseProgress findFirstByCourseIdAndUserId(Long courseId, Long userId);

    UserCourseProgress findFirstByCoursePackIdAndUserIdAndCourseId(Long coursePackId, Long userId, Long courseId);

    List<UserCourseProgress> findAllByUserId(Long userId);
}

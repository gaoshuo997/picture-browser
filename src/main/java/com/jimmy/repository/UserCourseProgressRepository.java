package com.jimmy.repository;

import com.jimmy.entity.UserCourseProgress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCourseProgressRepository extends JpaRepository<UserCourseProgress, Long> {
    UserCourseProgress findFirstByCourseIdAndUserId(Long courseId, Long userId);

    UserCourseProgress findFirstByCoursePackIdAndUserIdAndCourseId(Long coursePackId, Long userId, Long courseId);

    Page<UserCourseProgress> findByUserId(Long userId,Pageable pageable);
}

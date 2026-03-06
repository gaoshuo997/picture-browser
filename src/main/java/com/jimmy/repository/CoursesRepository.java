package com.jimmy.repository;

import com.jimmy.entity.Courses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoursesRepository extends JpaRepository<Courses, String> {

    List<Courses> findByCoursePackId(String coursePackId);

    Integer countByCoursePackId(String id);
}

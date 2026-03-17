package com.jimmy.repository;

import com.jimmy.entity.Courses;
import com.jimmy.entity.dto.CourseStateCountDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoursesRepository extends JpaRepository<Courses, Long> {

    Integer countByCoursePackId(Long id);

    @Query(value = "SELECT c.id as courseId,COUNT(s.id) as statementCount " +
            "FROM courses c " +
            "LEFT JOIN statements s ON c.id = s.course_id " +
            "WHERE c.id IN :courseIds " +
            "GROUP BY c.id", nativeQuery = true)
    List<CourseStateCountDTO> countStatementPreCourse(@Param("courseIds") List<Long> courseIds);

    Page<Courses> findByCoursePackId(Long coursePackId, Pageable pageable);
}

package com.jimmy.repository;

import com.jimmy.entity.Statements;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatementsRepository extends JpaRepository<Statements, Long>,
        JpaSpecificationExecutor<Statements> {
    List<Statements> findByCourseIdIn(List<Long> courseIdList);

    List<Statements> findByCourseId(Long courseId);
}

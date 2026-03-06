package com.jimmy.repository;

import com.jimmy.entity.CoursePacks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoursePacksRepository extends JpaRepository<CoursePacks, Long> {

}

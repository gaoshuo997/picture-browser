package com.jimmy.repository;

import com.jimmy.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    List<Menu> findByIdInAndDeleteFlag(List<Long> ids, Integer deleteFlag);

    List<Menu> findAllByDeleteFlag(Integer deleteFlag);
}
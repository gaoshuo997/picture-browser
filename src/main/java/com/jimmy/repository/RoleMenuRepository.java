package com.jimmy.repository;

import com.jimmy.entity.RoleMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleMenuRepository extends JpaRepository<RoleMenu, Long> {
    List<RoleMenu> findByRoleId(Long roleId);
    List<RoleMenu> findByRoleIdIn(List<Long> roleIds);
}

package com.jimmy.repository;

import com.jimmy.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    List<UserRole> findByUserId(Long userId);

    void deleteUserRoleByUserId(Long userId);

    List<UserRole> findByRoleId(Long roleId);
}

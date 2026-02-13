package com.jimmy.repository;

import com.jimmy.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {
    Page<Role> findByDeleteFlag(Integer deleteFlag, Pageable pageable);
}

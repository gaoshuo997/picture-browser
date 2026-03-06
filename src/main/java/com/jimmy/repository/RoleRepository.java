package com.jimmy.repository;

import com.jimmy.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {
    boolean existsRolesByRoleCode(String roleCode);

    boolean existsRolesByRoleName(String roleName);
}

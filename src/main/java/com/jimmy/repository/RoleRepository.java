package com.jimmy.repository;

import com.jimmy.entity.Role;
import com.jimmy.entity.dto.RoleMenuCountDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {
    boolean existsRolesByRoleCode(String roleCode);

    boolean existsRolesByRoleName(String roleName);

    @Query(value = "SELECT r.id as roleId, r.role_name as roleName, COUNT(rm.menu_id) as menuCount " +
            "FROM role r " +
            "LEFT JOIN role_menu rm ON r.id = rm.role_id " +
            "WHERE r.id IN :roleIds " +
            "GROUP BY r.id", nativeQuery = true)
    List<RoleMenuCountDTO> countMenusPerRole(@Param("roleIds") List<Long> roleIds);

    List<Role> findAllByIdInAndStatus(Set<Long> id, Integer status);

    List<Role> findRoleByStatus(Integer status);

    Optional<Role> findRoleByIdAndStatus(Long id, Integer status);
}

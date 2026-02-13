package com.jimmy.service.impl;

import com.jimmy.common.PaginatedApiResult;
import com.jimmy.entity.Role;
import com.jimmy.req.RoleReq;
import com.jimmy.resp.RoleResp;
import com.jimmy.repository.RoleRepository;
import com.jimmy.service.RoleService;
import com.jimmy.utils.DateUtils;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public PaginatedApiResult<RoleResp> list(Integer page, Integer pageSize) {

        Pageable pageable = PageRequest.of(page - 1, pageSize,
                Sort.by(Sort.Direction.DESC, "id"));
        RoleReq req = new RoleReq();
        Specification<Role> roleSpecification = buildSpecification(req);
        Page<Role> rolePage = roleRepository.findAll(roleSpecification, pageable);
        List<RoleResp> respList = rolePage.getContent().stream().map(role -> {
            RoleResp resp = new RoleResp();
            resp.setId(role.getId());
            resp.setRoleName(role.getRoleName());
            resp.setRoleCode(role.getRoleCode() != null ? role.getRoleCode().name() : null);
            resp.setDescription(role.getDescription());
            resp.setCreateAt(DateUtils.format(role.getCreateAt(),DateUtils.DATETIME_FORMAT));
            return resp;
        }).toList();

        return new PaginatedApiResult<>(page - 1, pageSize,
                respList.size(), respList);
    }

    /**
     * 构建查询条件
     * @param req 请求查询条件
     * @return 返回查询条件
     */
    private Specification<Role> buildSpecification(RoleReq req){
        return (root, query, cb) -> {
            // 存储查询条件的集合
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("deleteFlag"),0));
//            if (UserUtils.getUserId() != null){
//                predicates.add(cb.equal(root.get("userId"), UserUtils.getUserId()));
//            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

    }
}
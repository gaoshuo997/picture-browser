package com.jimmy.service.impl;

import com.jimmy.common.PaginatedApiResult;
import com.jimmy.common.exception.BadReqExceptionMsg;
import com.jimmy.common.result.BusinessException;
import com.jimmy.constant.DeleteFlag;
import com.jimmy.constant.PredicateFieldName;
import com.jimmy.constant.StatusFlag;
import com.jimmy.entity.Role;
import com.jimmy.entity.SignUser;
import com.jimmy.entity.UserRole;
import com.jimmy.entity.enums.RoleCode;
import com.jimmy.mapperStruct.SignUserMapper;
import com.jimmy.repository.RoleRepository;
import com.jimmy.repository.SignUserRepository;
import com.jimmy.repository.UserRoleRepository;
import com.jimmy.req.SignUserSave;
import com.jimmy.resp.RoleResp;
import com.jimmy.resp.SignUserResp;
import com.jimmy.security.SecurityUtils;
import com.jimmy.service.SignUserService;
import com.jimmy.utils.DateUtils;
import jakarta.annotation.Resource;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class SignUserServiceImpl implements SignUserService {

    @Resource
    private SignUserRepository signUserRepository;
    @Resource
    SignUserMapper signUserMapper;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private UserRoleRepository userRoleRepository;
    @Resource
    private RoleRepository roleRepository;

    @Override
    public SignUser insertSignUser(SignUserSave save) {
        long signUserNumber = signUserRepository.count();
        if (signUserNumber >= 1000){
            throw new BusinessException(BadReqExceptionMsg.SIGN_NUM_OVER.getCode(),
                    BadReqExceptionMsg.SIGN_NUM_OVER.getMessage());
        }
        Long countByLoginName = signUserRepository
                .countSignUsersByLoginNameIgnoreCaseAndDeleteFlag(save.getLoginName().trim(),DeleteFlag.NORMAL.getFlag());
        if (countByLoginName !=0 ){
            throw  new BusinessException(BadReqExceptionMsg.SIGN_ALREADY_EXIST.getCode(),
                    BadReqExceptionMsg.SIGN_ALREADY_EXIST.getMessage());
        }
        Long countByEmail = signUserRepository
                .countSignUsersByEmailIgnoreCaseAndDeleteFlag(save.getEmail().trim(),DeleteFlag.NORMAL.getFlag());
        if (countByEmail != 0){
            throw  new BusinessException(BadReqExceptionMsg.SiGN_EMAIL_EXIST.getCode(),
                    BadReqExceptionMsg.SiGN_EMAIL_EXIST.getMessage());
        }
        save.setPassword(passwordEncoder.encode(save.getPassword()));
        SignUser signUser = signUserMapper.saveToEntity(save);
        LocalDateTime now = LocalDateTime.now();
        signUser.setCreatedAt(now);
        signUser.setUpdatedAt(now);
        return signUserRepository.save(signUser);
    }

    /**
     * 根据ID查询没有被删除的用户，禁用的用户也可被查到
     * @param id 用户ID
     * @return 用户
     */
    @Override
    public SignUser findSignUserById(Long id) {
        if (id != null){
            return signUserRepository.findSignUsersByIdAndDeleteFlag(id,DeleteFlag.NORMAL.getFlag());
        }
        return null;
    }

    @Override
    public SignUser checkSignUser(String loginUserName, String password) {
        SignUser signUser = signUserRepository.findSignUserByLoginNameIgnoreCaseAndStatus(loginUserName, StatusFlag.VALID.getFlag());
        if (signUser == null){
            throw new BusinessException(BadReqExceptionMsg.SIGN_USER_NOT_EXIST.getCode(),
                    BadReqExceptionMsg.SIGN_USER_NOT_EXIST.getMessage());
        }
        if (!passwordEncoder.matches(password, signUser.getPassword())){
            throw new BusinessException(BadReqExceptionMsg.PASSWORD_ERROR.getCode(),
                    BadReqExceptionMsg.PASSWORD_ERROR.getMessage());
        }
        return signUser;
    }

    @Override
    public PaginatedApiResult<SignUserResp> list(Integer page, Integer size,
                                                 String loginName, LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate != null && endDate != null){
            if (endDate.isBefore(startDate)) {
                throw new BusinessException(BadReqExceptionMsg.INVALID_DATE_RANGE.getCode(),
                        BadReqExceptionMsg.INVALID_DATE_RANGE.getMessage());
            }
        }

        // 构建查询条件
        Specification<SignUser> signUserSpecification = buildSpecification(loginName, startDate, endDate);
        Pageable pageable = PageRequest.of(page - 1, size,
                Sort.by(Sort.Direction.DESC, PredicateFieldName.CREATED_AT.getName()));

        Page<SignUser> pageList = signUserRepository.findAll(signUserSpecification,pageable);
        List<SignUserResp> respList = new ArrayList<>(pageList.getSize());

        for (SignUser signUser : pageList.getContent()) {
            SignUserResp resp = new SignUserResp();
            resp.setCreatedAt(DateUtils.format(signUser.getCreatedAt(), DateUtils.DATETIME_FORMAT));
            resp.setUpdatedAt(DateUtils.format(signUser.getUpdatedAt(), DateUtils.DATETIME_FORMAT));
            BeanUtils.copyProperties(signUser, resp);
            respList.add(resp);
        }
        return new PaginatedApiResult<>(pageable.getPageNumber(),pageable.getPageSize(),
                respList.size(),pageList.getTotalElements(),
                respList,pageList.getTotalPages());
    }

    @Override
    public void deleteById(Long id) {
        SignUser signUser = checkUserIsExist(id);
        signUser.setDeleteFlag(DeleteFlag.DELETE.getFlag());
        signUser.setUpdatedAt(LocalDateTime.now());
        signUser.setStatus(StatusFlag.INVALID.getFlag());
        signUserRepository.save(signUser);
        userRoleRepository.deleteUserRoleByUserId(id);
    }

    @Override
    public void update(Long id, SignUserSave save) {
        SignUser signUser = checkUserIsExist(id);
        BeanUtils.copyProperties(save,signUser);
        signUser.setUpdatedAt(LocalDateTime.now());
        signUserRepository.save(signUser);
    }

    @Override
    public List<Long> getRolesByUserId(Long id) {
        checkUserIsExist(id);
        List<UserRole> byUserId = userRoleRepository.findByUserId(id);
        return byUserId.stream().map(UserRole::getRoleId).toList();
    }

    @Override
    public List<RoleResp> getRolesByOwner(Long id) {
        checkUserIsExist(id);

        // 如果是超级管理员，返回所有角色
        if (SecurityUtils.hasRole(RoleCode.ADMIN.toString())){
            return roleRepository.findRoleByStatus(StatusFlag.VALID.getFlag()).stream().map(role -> {
                RoleResp resp = new RoleResp();
                BeanUtils.copyProperties(role, resp);
                return resp;
            }).collect(Collectors.toList());
        }
        List<UserRole> byUserId = userRoleRepository.findByUserId(id);
        if (CollectionUtils.isEmpty(byUserId)){
            return List.of();
        }

        List<Role> allRoleById = roleRepository.findAllByIdInAndStatus(byUserId.stream()
                .map(UserRole::getRoleId).collect(Collectors.toSet()), StatusFlag.VALID.getFlag());

        // 返回自己拥有的角色列表
        return allRoleById.stream().map(role -> {
            RoleResp resp = new RoleResp();
            BeanUtils.copyProperties(role, resp);
            return resp;
        }).collect(Collectors.toList());
    }

    @Override
    public void assignRoles(Long id, Set<Long> roleIds) {
        SignUser signUser = checkUserIsExist(id);
        LocalDateTime now = LocalDateTime.now();
        // 删除旧的分配角色
        userRoleRepository.deleteUserRoleByUserId(signUser.getId());

        if (!CollectionUtils.isEmpty(roleIds)){
            List<UserRole> saves = new ArrayList<>(roleIds.size());
            for (Long roleId : roleIds) {
                UserRole userRole = new UserRole();
                userRole.setUserId(signUser.getId());
                userRole.setRoleId(roleId);
                userRole.setCreateAt(now);
                saves.add(userRole);
            }
            userRoleRepository.saveAll(saves);
        }
    }

    @Override
    public void setStatus(Long id) {
        SignUser signUser = checkUserIsExist(id);
        if (Objects.equals(signUser.getStatus(), StatusFlag.VALID.getFlag())){
            signUser.setStatus(StatusFlag.INVALID.getFlag());
        }else {
            signUser.setStatus(StatusFlag.VALID.getFlag());
        }
        signUser.setUpdatedAt(LocalDateTime.now());
        signUserRepository.save(signUser);
    }


    /**
     * 检查用户是否没被删除和没被禁用
     * @param id 用户ID
     * @return 用户
     */
    private SignUser checkUserIsExist(Long id){
        return signUserRepository.findByIdAndStatus(id, StatusFlag.VALID.getFlag()).orElseThrow(() ->
                new BusinessException(BadReqExceptionMsg.SIGN_USER_NOT_EXIST.getCode(),
                        BadReqExceptionMsg.SIGN_USER_NOT_EXIST.getMessage()));
    }

    /**
     * 构建查询条件
     * @param loginName 登录名
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return specification
     */
    private Specification<SignUser> buildSpecification(String loginName, LocalDateTime startDate,
                                                       LocalDateTime endDate){
        return (root, query, cb) -> {
            // 存储查询条件的集合
            List<Predicate> predicates = new ArrayList<>();
            if (loginName != null && !loginName.trim().isEmpty()){
                String safeName = loginName.trim();
                // 转义特殊字符 % 和 _，防止用户输入干扰模糊匹配逻辑
                safeName = safeName.replace("\\", "\\\\")
                        .replace("%", "\\%")
                        .replace("_", "\\_");
                predicates.add(cb.like(root.get(PredicateFieldName.LOGIN_NAME.getName()), cb.literal(safeName + "%")));
            }
            if (startDate != null){
                predicates.add(cb.greaterThanOrEqualTo(root.get(PredicateFieldName.CREATED_AT.getName()),startDate));
            }
            if (endDate != null){
                predicates.add(cb.lessThanOrEqualTo(root.get(PredicateFieldName.CREATED_AT.getName()), endDate));
            }
            predicates.add(cb.equal(root.get(PredicateFieldName.DELETE_FLAG.getName()), DeleteFlag.NORMAL.getFlag()));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}

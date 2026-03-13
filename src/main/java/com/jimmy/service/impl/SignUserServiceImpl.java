package com.jimmy.service.impl;

import com.jimmy.common.PaginatedApiResult;
import com.jimmy.common.exception.BadReqExceptionMsg;
import com.jimmy.common.result.BusinessException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
                .countSignUsersByLoginNameIgnoreCaseAndDeleteFlag(save.getLoginName().trim(),0);
        if (countByLoginName !=0 ){
            throw  new BusinessException(BadReqExceptionMsg.SIGN_ALREADY_EXIST.getCode(),
                    BadReqExceptionMsg.SIGN_ALREADY_EXIST.getMessage());
        }
        Long countByEmail = signUserRepository
                .countSignUsersByEmailIgnoreCaseAndDeleteFlag(save.getEmail().trim(),0);
        if (countByEmail != 0){
            throw  new BusinessException(BadReqExceptionMsg.SiGN_EMAIL_EXIST.getCode(),
                    BadReqExceptionMsg.SiGN_EMAIL_EXIST.getMessage());
        }
        save.setPassword(passwordEncoder.encode(save.getPassword()));
        SignUser signUser = signUserMapper.saveToEntity(save);
        LocalDateTime now = LocalDateTime.now();
        signUser.setCreateTime(now);
        signUser.setUpdateTime(now);
        return signUserRepository.save(signUser);
    }

    @Override
    public SignUser findSignUserById(Long id) {
        if (id != null){
            return signUserRepository.findSignUsersByIdAndDeleteFlag(id,0);
        }
        return null;
    }

    @Override
    public SignUser checkSignUser(String loginUserName, String password) {
        SignUser signUser = signUserRepository.findSignUserByLoginNameIgnoreCaseAndDeleteFlag(loginUserName, 0);
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
                Sort.by(Sort.Direction.DESC, "createTime"));

        Page<SignUser> pageList = signUserRepository.findAll(signUserSpecification,pageable);
        List<SignUserResp> respList = new ArrayList<>(pageList.getSize());

        for (SignUser signUser : pageList.getContent()) {
            SignUserResp resp = new SignUserResp();
            resp.setCreateTime(DateUtils.format(signUser.getCreateTime(), DateUtils.DATETIME_FORMAT));
            resp.setUpdateTime(DateUtils.format(signUser.getUpdateTime(), DateUtils.DATETIME_FORMAT));
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
        signUser.setDeleteFlag(1);
        signUserRepository.save(signUser);
        userRoleRepository.deleteUserRoleByUserId(id);
    }

    @Override
    public void update(Long id, SignUserSave save) {
        SignUser signUser = checkUserIsExist(id);
        BeanUtils.copyProperties(save,signUser);
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
        List<UserRole> byUserId = userRoleRepository.findByUserId(id);
        if (CollectionUtils.isEmpty(byUserId)){
            return List.of();
        }

        List<Role> allRoleById = roleRepository.findAllById(byUserId.stream()
                .map(UserRole::getRoleId).collect(Collectors.toSet()));
        // 查看用户是否拥有超级管理员权限
        Optional<Role> any = allRoleById.stream()
                .filter(role -> role.getRoleCode().equals(RoleCode.ADMIN.toString())).findAny();

        // 超级管理员拥有所有角色列表
        if (any.isPresent()){
            return roleRepository.findAll().stream().map(role -> {
                RoleResp resp = new RoleResp();
                BeanUtils.copyProperties(role, resp);
                return resp;
            }).collect(Collectors.toList());
        }

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

    /**
     * 检查角色是否存在
     */
    private SignUser checkUserIsExist(Long id){
        return signUserRepository.findById(id).orElseThrow(() ->
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
                predicates.add(cb.like(root.get("loginName"), cb.literal(safeName + "%")));
            }
            if (startDate != null){
                predicates.add(cb.greaterThanOrEqualTo(root.get("createTime"),startDate));
            }
            if (endDate != null){
                predicates.add(cb.lessThanOrEqualTo(root.get("createTime"), endDate));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}

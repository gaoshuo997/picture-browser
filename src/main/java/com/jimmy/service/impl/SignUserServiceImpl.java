package com.jimmy.service.impl;

import com.jimmy.common.exception.BadReqExceptionMsg;
import com.jimmy.common.result.BusinessException;
import com.jimmy.entity.SignUser;
import com.jimmy.mapperStruct.SignUserMapper;
import com.jimmy.repository.SignUserRepository;
import com.jimmy.req.SignUserReq;
import com.jimmy.service.SignUserService;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class SignUserServiceImpl implements SignUserService {

    @Resource
    private SignUserRepository signUserRepository;

    @Resource
    SignUserMapper signUserMapper;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public SignUser insertSignUser(SignUserReq req) {
        long signUserNumber = signUserRepository.count();
        if (signUserNumber >= 1000){
            throw new BusinessException(BadReqExceptionMsg.SIGN_NUM_OVER.getCode(),
                    BadReqExceptionMsg.SIGN_NUM_OVER.getMessage());
        }
        Long countByLoginName = signUserRepository
                .countSignUsersByLoginNameIgnoreCaseAndDeleteFlag(req.getLoginName().trim(),0);
        if (countByLoginName !=0 ){
            throw  new BusinessException(BadReqExceptionMsg.SIGN_ALREADY_EXIST.getCode(),
                    BadReqExceptionMsg.SIGN_ALREADY_EXIST.getMessage());
        }
        Long countByEmail = signUserRepository
                .countSignUsersByEmailIgnoreCaseAndDeleteFlag(req.getEmail().trim(),0);
        if (countByEmail != 0){
            throw  new BusinessException(BadReqExceptionMsg.SiGN_EMAIL_EXIST.getCode(),
                    BadReqExceptionMsg.SiGN_EMAIL_EXIST.getMessage());
        }
        req.setPassword(passwordEncoder.encode(req.getPassword()));
        SignUser signUser = signUserMapper.reqToEntity(req);
        Date now = new Date();
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
}

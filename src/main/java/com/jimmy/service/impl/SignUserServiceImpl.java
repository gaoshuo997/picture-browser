package com.jimmy.service.impl;

import com.jimmy.common.core.BadRequestException;
import com.jimmy.common.exception.BadReqExceptionMsg;
import com.jimmy.entity.SignUser;
import com.jimmy.mapperStruct.SignUserMapper;
import com.jimmy.repository.SignUserRepository;
import com.jimmy.req.SignUserReq;
import com.jimmy.service.SignUserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SignUserServiceImpl implements SignUserService {

    @Resource
    private SignUserRepository signUserRepository;

    @Resource
    SignUserMapper signUserMapper;

    @Override
    public SignUser insertSignUser(SignUserReq req) {
        long signUserNumber = signUserRepository.count();
        if (signUserNumber >= 1000){
            throw new BadRequestException(BadReqExceptionMsg.SIGN_NUM_OVER.getCode(),
                    BadReqExceptionMsg.SIGN_NUM_OVER.getMessage(), BadReqExceptionMsg.SIGN_NUM_OVER.getMessage());
        }
        Long countByLoginName = signUserRepository.countSignUsersByLoginNameIgnoreCase(req.getLoginName().trim());
        if (countByLoginName !=0 ){
            throw  new BadRequestException(BadReqExceptionMsg.SIGN_ALREADY_EXIST.getCode(),
                    BadReqExceptionMsg.SIGN_ALREADY_EXIST.getMessage(), BadReqExceptionMsg.SIGN_ALREADY_EXIST.getMessage());
        }
        Long countByEmail = signUserRepository.countSignUsersByEmailIgnoreCase(req.getEmail().trim());
        if (countByEmail != 0){
            throw  new BadRequestException(BadReqExceptionMsg.SiGN_EMAIL_EXIST.getCode(),
                    BadReqExceptionMsg.SiGN_EMAIL_EXIST.getMessage(), BadReqExceptionMsg.SiGN_EMAIL_EXIST.getMessage());
        }
        SignUser signUser = signUserMapper.reqToEntity(req);
        return signUserRepository.save(signUser);
    }
}

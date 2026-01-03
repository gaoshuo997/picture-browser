package com.jimmy.mapperStruct;

import com.jimmy.entity.SignUser;
import com.jimmy.req.SignUserReq;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface SignUserMapper {

    @Mapping(source = "loginName", target = "loginName", qualifiedByName = "stringTrim")
    @Mapping(source = "password", target = "password", qualifiedByName = "stringTrim")
    @Mapping(source = "email", target = "email", qualifiedByName = "stringTrim")
    SignUser reqToEntity(SignUserReq req);

    SignUserReq entityToReq(SignUser signUser);

    @Named("stringTrim")
    default String stringTrim(String source) {
        // 非空判断，避免NPE
        return source != null ? source.trim() : null;
    }
}

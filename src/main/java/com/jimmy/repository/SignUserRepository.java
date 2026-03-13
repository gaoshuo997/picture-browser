package com.jimmy.repository;

import com.jimmy.entity.SignUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SignUserRepository extends JpaRepository<SignUser, Long>, JpaSpecificationExecutor<SignUser> {

    Long countSignUsersByLoginNameIgnoreCaseAndDeleteFlag(String loginName,Integer flag);

    Long countSignUsersByEmailIgnoreCaseAndDeleteFlag(String email,Integer flag);

    SignUser findSignUsersByIdAndDeleteFlag(Long id, Integer deleteFlag);

//    SignUser findSignUserByLoginNameIgnoreCaseAndDeleteFlag(String loginName, Integer flag);

    Optional<SignUser> findByIdAndStatus(Long id, Integer status);

    SignUser findSignUserByLoginNameIgnoreCaseAndStatus(String loginUserName, Integer status);

}

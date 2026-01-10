package com.jimmy.repository;

import com.jimmy.entity.SignUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SignUserRepository extends JpaRepository<SignUser, Long> {

    Long countSignUsersByLoginNameIgnoreCaseAndDeleteFlag(String loginName,Integer flag);

    Long countSignUsersByEmailIgnoreCaseAndDeleteFlag(String email,Integer flag);

    SignUser findSignUsersByIdAndDeleteFlag(Long id, Integer deleteFlag);

}

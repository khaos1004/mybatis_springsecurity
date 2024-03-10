package com.isys.api.common.login.mapper;

import com.isys.api.common.login.dto.LoginRequestDTO;
import com.isys.api.common.login.dto.LoginResponseDTO;
import com.isys.api.common.login.dto.UserAuthInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.security.core.userdetails.User;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

@Mapper
public interface LoginMapper {
    /**
     * 로그인
     */
    Optional<LoginResponseDTO> login(String id, String password);

    @Select("SELECT NAME, PASSWORD FROM user_info WHERE NAME = #{id}")
    UserAuthInfo findByName(String username);

    @Select("SELECT PASSWORD FROM user_info")
//    @Select("SELECT PASSWORD FROM user_info WHERE PASSWORD = #{password}")
    UserAuthInfo findByPassword(String username);

}

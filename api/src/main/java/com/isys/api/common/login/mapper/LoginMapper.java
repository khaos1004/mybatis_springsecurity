package com.isys.api.common.login.mapper;

import com.isys.api.common.login.dto.LoginResponseDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface LoginMapper {
    /**
     * 로그인
     */
    Optional<LoginResponseDTO> login(String id, String password);

}

package com.isys.api.common.login.mapper;

import com.isys.api.common.login.dto.LoginResponseDTO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.security.core.userdetails.User;

import java.util.Optional;

@Mapper
public interface LoginMapper {
    /**
     * 로그인
     */
    Optional<LoginResponseDTO> login(String id, String password);

    @Mapper
    public interface UserRepository {
        User findByUsername(String username);
    }

}

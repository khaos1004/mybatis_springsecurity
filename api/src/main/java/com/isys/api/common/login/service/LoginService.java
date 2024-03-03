package com.isys.api.common.login.service;

import com.isys.api.common.jwt.JwtTokenProvider;
import com.isys.api.common.login.dto.LoginResponseDTO;
import com.isys.api.common.login.mapper.LoginMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final LoginMapper loginMapper;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    public Optional<LoginResponseDTO> login(String id, String password) {
        return loginMapper.login(id, password);
    }
}

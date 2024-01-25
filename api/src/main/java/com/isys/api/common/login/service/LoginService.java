package com.isys.api.common.login.service;

import com.isys.api.common.login.dto.LoginResponseDTO;
import com.isys.api.common.login.mapper.LoginMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final LoginMapper loginMapper;

    public Optional<LoginResponseDTO> login(String name, String password) {
        return loginMapper.login(name, password);
    }
}

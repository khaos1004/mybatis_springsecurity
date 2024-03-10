package com.isys.api.common.login.service;

import com.isys.api.common.login.dto.JwtToken;
import com.isys.api.common.login.dto.LoginRequestDTO;
import com.isys.api.common.login.dto.LoginResponseDTO;
import com.isys.api.common.login.mapper.LoginMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final LoginMapper loginMapper;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public Optional<LoginResponseDTO> login(String id, String password) {
        return loginMapper.login(id, password);
    }

    @Transactional
    public JwtToken signIn(LoginRequestDTO loginRequest) {
        // 1. username + password 를 기반으로 Authentication 객체 생성
        // 이때 authentication 은 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getId(), loginRequest.getPassword());

        // 2. 실제 검증. authenticate() 메서드를 통해 요청된 Member 에 대한 검증 진행
        // authenticate 메서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
//        return jwtTokenProvider.generateToken(authentication);
        return null;
    }
}

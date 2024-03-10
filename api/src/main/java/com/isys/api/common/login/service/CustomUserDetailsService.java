package com.isys.api.common.login.service;

import com.isys.api.common.login.dto.CustomUserDetails;
import com.isys.api.common.login.dto.LoginRequestDTO;
import com.isys.api.common.login.dto.UserAuthInfo;
import com.isys.api.common.login.mapper.LoginMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final LoginMapper loginMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {

        UserAuthInfo userData = loginMapper.findByName(id);

            // userData가 null이 아니면 CustomUserDetails 객체 반환
            if (userData != null) {
                return new CustomUserDetails(userData);
            } else {
                // userData가 null인 경우 UsernameNotFoundException 예외 던짐
                throw new UsernameNotFoundException("User not found with username: " + id);
            }
        }

//        return loginMapper.findByName(id)
//                .map(this::createUserDetails)
//                .orElseThrow(() -> new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다."));

    }

    // 해당하는 User 의 데이터가 존재한다면 UserDetails 객체로 만들어서 return
//    private UserDetails createUserDetails(LoginRequestDTO loginRequestDTO) {
//        return User.builder()
//                .username(loginRequestDTO.getId())
//                .password(passwordEncoder.encode(loginRequestDTO.getPassword()))
////                .roles(member.getRoles().toArray(new String[0]))
//                .build();
//    }
//}
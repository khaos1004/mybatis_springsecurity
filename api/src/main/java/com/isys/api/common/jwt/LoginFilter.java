package com.isys.api.common.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isys.api.common.login.dto.CustomUserDetails;
import com.isys.api.common.login.dto.LoginRequestDTO;
import com.isys.api.common.login.dto.UserAuthInfo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {

        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        setUsernameParameter("id");
        setPasswordParameter("password");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        try {
            // 요청 본문에서 JSON 데이터를 읽어와 User 객체로 변환
            LoginRequestDTO loginRequest = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDTO.class);

            // User 객체에서 username과 password 추출
            String username = loginRequest.getId();
            String password = loginRequest.getPassword();

            System.out.println(loginRequest);
            System.out.println(username);
            System.out.println(password);

            // 추출한 정보를 바탕으로 UsernamePasswordAuthenticationToken 생성
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

            // AuthenticationManager를 사용하여 인증 시도
            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            throw new AuthenticationServiceException("Failed to parse authentication request body", e);
        }
        //아래는 폼로그인
//        String username = request.
//        String password = obtainPassword(request);
//
//        System.out.println(obtainUsername(request));
////        log.info(String.valueOf(obtainUsername(request)));
//
//        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
//
//        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {

        String NAME = "";
        String USER_ID = "";
        String USERGROUP_FK = "";

        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            // CustomUserDetails를 통해 UserAuthInfo에서 정보 가져오기
            NAME = userDetails.getUsername(); // 이미 UserDetails 인터페이스에 정의된 메소드를 통해 사용자 이름을 가져옴
            USER_ID = userDetails.getUserId(); // CustomUserDetails에서 정의한 메소드를 통해 userId 가져오기
            USERGROUP_FK = userDetails.getUserGroupFk(); // CustomUserDetails에서 정의한 메소드를 통해 userGroupFk 가져오기
        }


        String jsonResponse = String.format("{\"OK\": \"true\", \"NAME\": \"%s\", \"USER_ID\": \"%s\", \"USERGROUP_FK\": \"%s\"}", NAME, USER_ID, USERGROUP_FK);

        //유저 정보
//        String NAME = authentication.getName();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
       //String role = auth.getAuthority();

        //토큰 생성
        String access = jwtUtil.createJwt("accessToken", NAME, 1800000L);
        String refresh = jwtUtil.createJwt("refreshToken", NAME, 36000000L);

        // 리프레쉬 토큰 쿠키 설정
        Cookie refreshCookie = new Cookie("refreshToken", refresh);
        refreshCookie.setMaxAge(10 * 60 * 60); // 10시간
        refreshCookie.setSecure(false); // HTTPS 통신일 경우만 쿠키 전송
        refreshCookie.setPath("/");
        refreshCookie.setHttpOnly(true);
        // SameSite=None을 추가하여 크로스 사이트 요청 시 쿠키가 전송되도록 설정
        String refreshCookieString = String.format("%s=%s; Path=%s; Max-Age=%d; HttpOnly; SameSite=Lax",
                refreshCookie.getName(), refreshCookie.getValue(), refreshCookie.getPath(), refreshCookie.getMaxAge());
        response.addHeader("Set-Cookie", refreshCookieString);

        // 액세스 토큰 쿠키 설정
        Cookie accessCookie = new Cookie("accessToken", access);
        accessCookie.setMaxAge(30 * 60); // 30분
        accessCookie.setSecure(false); // HTTPS 통신일 경우만 쿠키 전송
        accessCookie.setPath("/");
        accessCookie.setHttpOnly(true);
        // SameSite=None을 추가하여 크로스 사이트 요청 시 쿠키가 전송되도록 설정
        String accessCookieString = String.format("%s=%s; Path=%s; Max-Age=%d; HttpOnly; SameSite=Lax",
                accessCookie.getName(), accessCookie.getValue(), accessCookie.getPath(), accessCookie.getMaxAge());
        response.addHeader("Set-Cookie", accessCookieString);


        //응답 설정
//        response.setHeader("accessToken", access); // 액세스 토큰을 HTTP 헤더에 설정
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
        response.setStatus(HttpStatus.OK.value());
    }

    private Cookie createRefeshCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(10*60*60);
//        cookie.setSecure(true); //https 통신일경우 설정
        cookie.setPath("/");
        cookie.setSecure(false);
        cookie.setHttpOnly(true);

        return cookie;
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {

        String jsonResponsefail = "{\"OK\": \"false\"}";
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponsefail);
        response.getWriter().flush();
        response.setStatus(401);
    }
}

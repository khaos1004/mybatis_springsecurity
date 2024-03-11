package com.isys.api.common.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isys.api.common.login.dto.LoginRequestDTO;
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

        String jsonResponse = "{\"OK\": \"true\"}";

        //유저 정보
        String username = authentication.getName();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
       //String role = auth.getAuthority();

        Cookie accessCookie = createAccessCookie("accessToken", "token_value");
        // SameSite=None과 Secure 설정을 포함한 쿠키 문자열 생성
        String cookieString = String.format("%s=%s; Path=%s; Max-Age=%d; Secure; HttpOnly; SameSite=None",
                accessCookie.getName(), accessCookie.getValue(), accessCookie.getPath(), accessCookie.getMaxAge());

        Cookie refreshCookie = createAccessCookie("accessToken", "token_value");
        // SameSite=None과 Secure 설정을 포함한 쿠키 문자열 생성
        String refreshCookieString = String.format("%s=%s; Path=%s; Max-Age=%d; Secure; HttpOnly; SameSite=None",
                accessCookie.getName(), accessCookie.getValue(), accessCookie.getPath(), accessCookie.getMaxAge());

        //토큰 생성
        String access = jwtUtil.createJwt("accessToken", username, 1800000L);
//        String access = jwtUtil.createJwt("accessToken", username, 30000L);
        String refresh = jwtUtil.createJwt("refreshToken", username, 36000000L);

        //응답 설정
        response.addHeader("Set-Cookie", cookieString);
        response.addHeader("Set-Cookie", refreshCookieString);
        response.setHeader("accessToken", access);
        response.addCookie(createAccessCookie("accessToken", access));
        response.addCookie(createRefeshCookie("refreshToken", refresh));
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
        response.setStatus(HttpStatus.OK.value());

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

    private Cookie createAccessCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(30*60);
//        cookie.setSecure(true); //https 통신일경우 설정
        cookie.setPath("/");
        cookie.setHttpOnly(true);


        return cookie;
    }

    private Cookie createRefeshCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(10*60*60);
//        cookie.setSecure(true); //https 통신일경우 설정
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}

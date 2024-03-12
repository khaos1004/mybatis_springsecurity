package com.isys.api.common.jwt;

import com.isys.api.common.login.dto.CustomUserDetails;
import com.isys.api.common.login.dto.LoginRequestDTO;
import com.isys.api.common.login.dto.LoginResponseDTO;
import com.isys.api.common.login.dto.UserAuthInfo;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@AllArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 헤더에서 access키에 담긴 토큰을 꺼냄
//        String accessToken = request.getHeader("accessToken");

        String accessToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                    break; // 쿠키를 찾았으므로 반복 중단
                }
            }
        }


        // 토큰이 없다면 다음 필터로 넘김
        if (accessToken == null) {

            filterChain.doFilter(request, response);

            return;
        }

        // 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {

            //response body
            PrintWriter writer = response.getWriter();
            writer.print("access token expired");

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

//        try {
//            jwtUtil.isExpired(accessToken);
//        } catch (ExpiredJwtException e) {
//            handleRefreshToken(request, response);
//            return; // 이후 처리 중단
//        }

        // 토큰이 access인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals("accessToken")) {

            //response body
            PrintWriter writer = response.getWriter();
            writer.print("invalid access token");

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // username, role 값을 획득
        String username = jwtUtil.getUsername(accessToken);
        //String role = jwtUtil.getRole(accessToken);

        UserAuthInfo userAuthInfo = new UserAuthInfo();
        userAuthInfo.setNAME(username);
//        userEntity.setRole(role);
        CustomUserDetails customUserDetails = new CustomUserDetails(userAuthInfo);

        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }


    private void handleRefreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String refresh = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
//                System.out.println("Cookie Name: " + cookie.getName() + ", Value: " + cookie.getValue());
                if ("refreshToken".equals(cookie.getName())) {
                    refresh = cookie.getValue();
//                    System.out.println("Found refreshToken: " + refresh); // refreshToken 찾았을 때 출력
                    break; // 쿠키를 찾으면 반복 중단
                }
            }
        }

        System.out.println(refresh);
        if (refresh == null) {

            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Refresh token is null");
            return;
        }

        //expired check
        try {
            if (jwtUtil.isExpired(refresh)) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Refresh token expired");
                return;
            }
        } catch (ExpiredJwtException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Refresh token expired");
            return;
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refresh);

        if (!"refreshToken".equals(category)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid refresh token");
            return;
        }

        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);

        //make new JWT
        String access = jwtUtil.createJwt("accessToken", username, 1800000L);

        //response
        response.setHeader("accessToken", access);
        response.addCookie(createAccessCookie("accessToken", access));
        response.setStatus(HttpServletResponse.SC_OK); // HTTP 상태 코드를 200(OK)으로 설정

        /*(선택사항) 응답 바디에 메시지 추가
           PrintWriter writer = response.getWriter();
           writer.write("New access token created");
           writer.flush();
        */
    }

    private Cookie createAccessCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(30*60);
        //cookie.setSecure(true); //https 통신일경우 설정
        //cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}

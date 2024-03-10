package com.isys.api.common.login.controller;

import com.isys.api.common.login.dto.JwtToken;
import com.isys.api.common.login.dto.LoginRequestDTO;
import com.isys.api.common.login.dto.LoginResponseDTO;
import com.isys.api.common.login.dto.UserRequestDTO;
import com.isys.api.common.login.service.LoginService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class LoginController {
    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest, HttpServletRequest request, HttpServletResponse response) {

            String username = loginRequest.getId();
            String password = loginRequest.getPassword();
            Map<String, Object> obj = new HashMap<>();

            Optional<LoginResponseDTO> userInfo = loginService.login(
                    loginRequest.getId(),
                    loginRequest.getPassword()
            );

            if (userInfo.isPresent()) {
                JwtToken jwtToken = loginService.signIn(loginRequest);
                log.info("request username = {}, password = {}", username, password);
                log.info("jwtToken accessToken = {}, refreshToken = {}", jwtToken.getAccessToken(), jwtToken.getRefreshToken());

                // create a cookie
                Cookie cookieAccessToken = new Cookie("AccessToken", jwtToken.getAccessToken());
                Cookie cookieRefreshToken = new Cookie("RefreshToken", jwtToken.getRefreshToken());

                // set cookie properties and add them to the response
                cookieAccessToken.setMaxAge(60 * 60 * 10);
                cookieRefreshToken.setMaxAge(60 * 60 * 20); // expires in 10 hours
                cookieAccessToken.setPath("/");
                cookieRefreshToken.setPath("/");
                cookieAccessToken.setHttpOnly(true);
                cookieRefreshToken.setHttpOnly(true);

                response.addCookie(cookieAccessToken);
                response.addCookie(cookieRefreshToken);

                obj.put("OK","true");
                return new ResponseEntity<>(obj, HttpStatus.OK);
            }
            obj.put("OK", "false");
            obj.put("message", "인증 실패.");
            return ResponseEntity.status(401).body(obj.toString());
    }


    @GetMapping("/checkSession")
    public ResponseEntity<String> checkSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // 기존 세션을 가져옵니다. 세션이 없으면 null 반환

        if (session != null && session.getAttribute("loginMember") != null) {
            // 세션이 존재하고, 로그인한 사용자의 정보가 세션에 있는 경우
            return ResponseEntity.ok("Session is valid");
        } else {
            // 세션이 없거나, 로그인한 사용자의 정보가 없는 경우
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Session is invalid or expired");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {

        Map<String, Object> obj = new HashMap<>();
        // 기존의 AccessToken 및 RefreshToken 쿠키를 찾아서 만료시킴
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("AccessToken") || cookie.getName().equals("RefreshToken")) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0); // 쿠키를 즉시 만료시킴
                    response.addCookie(cookie); // 변경된 쿠키를 응답에 추가
                }
            }
        }
        obj.put("OK","true");
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }
}

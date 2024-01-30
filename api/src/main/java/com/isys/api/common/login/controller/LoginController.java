package com.isys.api.common.login.controller;

import com.isys.api.common.login.dto.LoginRequestDTO;
import com.isys.api.common.login.dto.LoginResponseDTO;
import com.isys.api.common.login.dto.UserRequestDTO;
import com.isys.api.common.login.service.LoginService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;

    @PostMapping("/login")
//    @CrossOrigin(origins = "http://192.168.0.143:3000", allowCredentials = "true")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequestDTO, HttpServletRequest request, HttpServletResponse response) {

        System.out.println(loginRequestDTO);

        JSONObject obj = new JSONObject();
        Optional<LoginResponseDTO> userInfo = loginService.login(
                loginRequestDTO.getName(),
                loginRequestDTO.getPassword()
        );

        if (userInfo.isPresent()) {
            // create a cookie
            Cookie cookieAccessToken = new Cookie("AccessToken","VlwEyVBsYt9V7zq57TejMnVUyzblYcfPQye08f7MGVA9XkHa");
            Cookie cookieRefreshToken = new Cookie("RefreshToken","DGerdsVSYt9V7zq57TejMnVUyzblYcfPQye08f7MGVA9XkHa");

            // expires in 1 hour
            cookieAccessToken.setMaxAge(60 * 60);
            cookieRefreshToken.setMaxAge(60 * 600);

            // optional properties
            cookieAccessToken.setSecure(false);
            cookieAccessToken.setHttpOnly(false);
            cookieAccessToken.setPath("/");
            cookieRefreshToken.setSecure(false);
            cookieRefreshToken.setHttpOnly(false);
            cookieRefreshToken.setPath("/");

            // add cookie to response
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
//    @CrossOrigin(origins = "http://192.168.0.143:3000", allowCredentials = "true")
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
//    @CrossOrigin(origins = "http://192.168.0.143:3000", allowCredentials = "true")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
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
        return ResponseEntity.ok("로그아웃 성공");
    }
}

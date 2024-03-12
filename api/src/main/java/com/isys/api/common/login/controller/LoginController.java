package com.isys.api.common.login.controller;

import com.isys.api.common.jwt.JWTUtil;
import com.isys.api.common.login.dto.JwtToken;
import com.isys.api.common.login.dto.LoginRequestDTO;
import com.isys.api.common.login.dto.LoginResponseDTO;
import com.isys.api.common.login.dto.UserRequestDTO;
import com.isys.api.common.login.service.LoginService;
import io.jsonwebtoken.ExpiredJwtException;
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
//@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class LoginController {
    private final LoginService loginService;
    private final JWTUtil jwtUtil;

    @PostMapping("/api/login")
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

    @PostMapping("/refresh")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        //아래코드는 서비스단으로 변경
        //get refresh token
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("refreshToken")) {

                refresh = cookie.getValue();
            }
        }

        if (refresh == null) {

            //response status code
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }

        //expired check
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {

            //response status code
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refresh);

        if (!category.equals("refreshToken")) {

            //response status code
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        String username = jwtUtil.getUsername(refresh);
//        String role = jwtUtil.getRole(refresh);

        //make new JWT
        String access = jwtUtil.createJwt("accessToken", username, 1800000L);

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

        //response
//        response.setHeader("accessToken", access);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PostMapping("/users/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {

        Map<String, Object> obj = new HashMap<>();
//        Cookie cookie = new Cookie("refreshToken", null); // 쿠키 이름과 동일하게
//        cookie.setPath("/");
//        cookie.setHttpOnly(true);
//        cookie.setSecure(true); // HTTPS 사용 시 true로 설정
//        cookie.setMaxAge(0); // 쿠키 만료시키기
//        response.addCookie(cookie);
        // 기존의 AccessToken 및 RefreshToken 쿠키를 찾아서 만료시킴
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("accessToken") || cookie.getName().equals("refreshToken")) {
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

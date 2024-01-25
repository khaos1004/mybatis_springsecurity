package com.isys.api.common.login.controller;

import com.isys.api.common.login.dto.LoginRequestDTO;
import com.isys.api.common.login.dto.LoginResponseDTO;
import com.isys.api.common.login.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;

    @PostMapping("/login")
    @CrossOrigin(origins = "*", allowCredentials = "true")
    public ResponseEntity<Optional<LoginResponseDTO>> login(@RequestBody LoginRequestDTO loginRequestDTO, HttpServletRequest request) {

        Optional<LoginResponseDTO> userInfo = loginService.login(
                loginRequestDTO.getNAME(),
                loginRequestDTO.getPASSWORD()
        );

        if (userInfo.isPresent()) {
            HttpSession session = request.getSession();
            session.setAttribute("loginMember", userInfo);
            LoginResponseDTO user = userInfo.get();
            return ResponseEntity.ok(userInfo);
        }

        return ResponseEntity.status(401).body(null);
    }


    @GetMapping("/checkSession")
    @CrossOrigin(origins = "http://localhost:8081", allowCredentials = "true")
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
    @CrossOrigin(origins = "*", allowCredentials = "true")
    public String logout(HttpSession session) {
        session.invalidate();
        return "logout!";
    }
}

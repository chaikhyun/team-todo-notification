package com.example.teamtodo.controller;

import com.example.teamtodo.jwt.JwtTokenProvider;
import com.example.teamtodo.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestBody Map<String, String> payload) {
        String refreshToken = payload.get("refreshToken");

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(401).body("유효하지 않은 리프레시 토큰입니다.");
        }

        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);

        String storedToken = authService.getRefreshToken(username);
        if (storedToken == null || !storedToken.equals(refreshToken)) {
            return ResponseEntity.status(401).body("리프레시 토큰이 일치하지 않습니다.");
        }

        String newAccessToken = jwtTokenProvider.generateToken(username);

        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    /**
     * 로그아웃 API
     * 클라이언트는 Authorization 헤더에 AccessToken을 포함해서 요청
     * 해당 사용자의 Redis에 저장된 RefreshToken 삭제 처리
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request); // 헤더에서 토큰 추출

        if (token == null || !jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.badRequest().body("유효하지 않은 토큰입니다.");
        }

        String username = jwtTokenProvider.getUsernameFromToken(token);

        // Redis에서 refreshToken 삭제
        authService.deleteRefreshToken(username);

        return ResponseEntity.ok("로그아웃 성공");
    }
}

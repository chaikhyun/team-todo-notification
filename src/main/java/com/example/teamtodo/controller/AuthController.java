package com.example.teamtodo.controller;

import com.example.teamtodo.jwt.JwtTokenProvider;
import com.example.teamtodo.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

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
        String token = jwtTokenProvider.resolveToken(request);

        if (token == null || !jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.badRequest().body("유효하지 않은 토큰입니다.");
        }

        String username = jwtTokenProvider.getUsernameFromToken(token);

        // 1. RefreshToken 삭제
        authService.deleteRefreshToken(username);

        // 2. AccessToken 블랙리스트 등록
        long expiration = jwtTokenProvider.getExpiration(token); // 토큰 만료까지 남은 시간(ms)
        redisTemplate.opsForValue().set(token, "logout", expiration, TimeUnit.MILLISECONDS);

        return ResponseEntity.ok("로그아웃 성공");
    }


}

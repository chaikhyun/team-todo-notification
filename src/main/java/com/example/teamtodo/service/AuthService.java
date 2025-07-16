package com.example.teamtodo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String REFRESH_TOKEN_PREFIX = "refresh:";

    public void saveRefreshToken(String username, String refreshToken, long expirationMillis) {
        redisTemplate.opsForValue().set(
                getKey(username),
                refreshToken,
                Duration.ofMillis(expirationMillis)
        );
    }

    public String getRefreshToken(String username) {
        return redisTemplate.opsForValue().get(getKey(username));
    }

    public void deleteRefreshToken(String username) {
        redisTemplate.delete("refresh:" + username);
    }


    private String getKey(String username) {
        return REFRESH_TOKEN_PREFIX + username;
    }
}

package com.example.teamtodo.controller;

import com.example.teamtodo.dto.UserLoginRequest;
import com.example.teamtodo.dto.UserSignupRequest;
import com.example.teamtodo.dto.UserResponse;
import com.example.teamtodo.jwt.JwtTokenProvider;
import com.example.teamtodo.service.AuthService;
import com.example.teamtodo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@Valid @RequestBody UserSignupRequest request) {
        UserResponse response = userService.signup(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

//    @PostMapping("/login")
//    public ResponseEntity<?> login(@Valid @RequestBody UserLoginRequest request) {
//        String username = userService.authenticate(request.getEmail(), request.getPassword());
//        String token = jwtTokenProvider.generateToken(username);
//        return ResponseEntity.ok().body(token);
//    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginRequest request) {
        String username = userService.authenticate(request.getEmail(), request.getPassword());

        String accessToken = jwtTokenProvider.generateToken(username);
        String refreshToken = jwtTokenProvider.generateRefreshToken(username);

        authService.saveRefreshToken(username, refreshToken, jwtTokenProvider.getRefreshTokenValidity());

        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken
        ));
    }

    @PostMapping("/oauth")
    public ResponseEntity<?> socialLogin(@RequestBody Map<String, String> payload) {
        String provider = payload.get("provider");
        String providerId = payload.get("providerId");
        String email = payload.get("email");
        String username = payload.get("username"); // nullable

        UserResponse user = userService.processOAuthPostLogin(provider, providerId, email, username);
        String token = jwtTokenProvider.generateToken(user.getUsername());

        return ResponseEntity.ok().body(token);
    }


}

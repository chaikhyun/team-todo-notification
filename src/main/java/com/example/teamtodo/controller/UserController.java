package com.example.teamtodo.controller;

import com.example.teamtodo.dto.UserLoginRequest;
import com.example.teamtodo.dto.UserSignupRequest;
import com.example.teamtodo.dto.UserResponse;
import com.example.teamtodo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@Valid @RequestBody UserSignupRequest request) {
        UserResponse response = userService.signup(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginRequest request) {
        // TODO: 실제 인증 로직 구현 필요 (JWT 발급 등)
        return ResponseEntity.ok("로그인 성공 (구현 예정)");
    }

    @PostMapping("/oauth")
    public ResponseEntity<UserResponse> socialLogin(
            @RequestParam String provider,
            @RequestParam String providerId,
            @RequestParam String email,
            @RequestParam(required = false) String username
    ) {
        UserResponse response = userService.processOAuthPostLogin(provider, providerId, email, username);
        return ResponseEntity.ok(response);
    }

}

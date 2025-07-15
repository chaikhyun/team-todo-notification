package com.example.teamtodo.controller;

import com.example.teamtodo.dto.UserLoginRequest;
import com.example.teamtodo.dto.UserSignupRequest;
import com.example.teamtodo.dto.UserResponse;
import com.example.teamtodo.jwt.JwtTokenProvider;
import com.example.teamtodo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@Valid @RequestBody UserSignupRequest request) {
        UserResponse response = userService.signup(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginRequest request) {
        String username = userService.authenticate(request.getEmail(), request.getPassword());
        String token = jwtTokenProvider.generateToken(username);
        return ResponseEntity.ok().body(token);
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

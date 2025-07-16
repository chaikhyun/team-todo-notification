package com.example.teamtodo.controller;

import com.example.teamtodo.domain.User;
import com.example.teamtodo.dto.UserLoginRequest;
import com.example.teamtodo.dto.UserSignupRequest;
import com.example.teamtodo.dto.UserResponse;
import com.example.teamtodo.dto.UserUpdateRequest;
import com.example.teamtodo.jwt.JwtTokenProvider;
import com.example.teamtodo.security.CustomUserDetails;
import com.example.teamtodo.service.AuthService;
import com.example.teamtodo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

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
    User user = userService.findByEmail(request.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 잘못되었습니다."));

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        throw new IllegalArgumentException("이메일 또는 비밀번호가 잘못되었습니다.");
    }

    String accessToken = jwtTokenProvider.generateToken(user.getEmail());
    String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

    authService.saveRefreshToken(user.getEmail(), refreshToken, jwtTokenProvider.getRefreshTokenValidity());

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

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        UserResponse response = new UserResponse(user.getUserId(), user.getUsername(), user.getEmail());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/username")
    public ResponseEntity<?> updateUsername(@AuthenticationPrincipal UserDetails userDetails,
                                            @RequestBody @Valid UserUpdateRequest request) {
        String email = userDetails.getUsername(); // email이 들어있음
        userService.updateUsername(email, request.getNewUsername());
        return ResponseEntity.ok("닉네임이 성공적으로 변경되었습니다.");
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUser(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        userService.deleteUser(email);
        return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
    }



}

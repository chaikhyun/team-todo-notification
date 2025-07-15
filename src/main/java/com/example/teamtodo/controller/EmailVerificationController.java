package com.example.teamtodo.controller;

import com.example.teamtodo.dto.EmailRequest;
import com.example.teamtodo.dto.VerifyCodeRequest;
import com.example.teamtodo.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    // 1) 인증 코드 발송
    @PostMapping("/send-code")
    public ResponseEntity<String> sendCode(@RequestBody EmailRequest request) {
        String code = emailVerificationService.generateVerificationCode();
        emailVerificationService.saveVerificationCode(request.getEmail(), code);
        emailVerificationService.sendVerificationEmail(request.getEmail(), code);

        return ResponseEntity.ok("인증 코드가 발송되었습니다.");
    }

    // 2) 인증 코드 검증
    @PostMapping("/verify-code")
    public ResponseEntity<String> verifyCode(@RequestBody VerifyCodeRequest request) {
        boolean verified = emailVerificationService.verifyCode(request.getEmail(), request.getCode());
        if (verified) {
            return ResponseEntity.ok("이메일 인증에 성공했습니다.");
        } else {
            return ResponseEntity.badRequest().body("인증 코드가 올바르지 않거나 만료되었습니다.");
        }
    }
}

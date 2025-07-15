package com.example.teamtodo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final JavaMailSender mailSender;
    private final StringRedisTemplate redisTemplate;

    private static final String REDIS_KEY_PREFIX = "email:verify:";
    private static final Duration VERIFICATION_CODE_EXPIRE = Duration.ofMinutes(5);

    // 인증 코드 생성
    public String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 6자리 숫자
        return String.valueOf(code);
    }

    // 인증 코드 저장 (Redis)
    public void saveVerificationCode(String email, String code) {
        String key = REDIS_KEY_PREFIX + email;
        redisTemplate.opsForValue().set(key, code, VERIFICATION_CODE_EXPIRE);
    }

    // 인증 코드 이메일 발송
    public void sendVerificationEmail(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("[TeamTodo] 이메일 인증 코드 발송");
        message.setText("인증 코드는 " + code + " 입니다. 5분 내에 입력해주세요.");

        mailSender.send(message);
    }

    // 인증 코드 검증
    public boolean verifyCode(String email, String code) {
        String key = REDIS_KEY_PREFIX + email;
        String savedCode = redisTemplate.opsForValue().get(key);
        if (savedCode != null && savedCode.equals(code)) {
            redisTemplate.delete(key);  // 인증 코드 삭제
            markEmailAsVerified(email); // 이메일 인증 성공 처리
            return true;
        }
        return false;
    }

    // 인증 완료 표시 (예: 인증 코드 확인 후 호출)
    public void markEmailAsVerified(String email) {
        String key = "email:verified:" + email;
        redisTemplate.opsForValue().set(key, "true", Duration.ofMinutes(30)); // 30분 유지 (필요 시 조정)
    }

    // 인증 여부 확인 (회원가입 시 호출)
    public boolean isEmailVerified(String email) {
        String key = "email:verified:" + email;
        String value = redisTemplate.opsForValue().get(key);
        return "true".equals(value);
    }

}

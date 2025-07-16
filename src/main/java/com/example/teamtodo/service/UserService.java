package com.example.teamtodo.service;

import com.example.teamtodo.domain.User;
import com.example.teamtodo.dto.UserSignupRequest;
import com.example.teamtodo.dto.UserResponse;
import com.example.teamtodo.exception.DuplicateResourceException;
import com.example.teamtodo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;


    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       EmailVerificationService emailVerificationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailVerificationService = emailVerificationService;
    }

    @Transactional
    public UserResponse signup(UserSignupRequest request) {
        if (!emailVerificationService.isEmailVerified(request.getEmail())) {
            throw new IllegalStateException("이메일 인증이 완료되지 않았습니다.");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("이미 존재하는 사용자명입니다.");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("이미 존재하는 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(encodedPassword);

        User savedUser = userRepository.save(user);

        return new UserResponse(savedUser.getUserId(), savedUser.getUsername(), savedUser.getEmail());
    }

    @Transactional
    public UserResponse processOAuthPostLogin(String provider, String providerId, String email, String username) {
        // 1. provider + providerId로 기존 사용자 조회
        Optional<User> userOpt = userRepository.findByProviderAndProviderId(provider, providerId);

        if (userOpt.isPresent()) {
            return new UserResponse(
                    userOpt.get().getUserId(),
                    userOpt.get().getUsername(),
                    userOpt.get().getEmail()
            );
        }

        // 2. 이메일 중복 여부 확인
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("이미 해당 이메일로 가입된 계정이 존재합니다.");
        }

        // 3. 신규 사용자 생성
        User user = new User();
        user.setProvider(provider);
        user.setProviderId(providerId);
        user.setEmail(email);
        user.setUsername(username != null ? username : email);
        user.setPassword(""); // 소셜 로그인은 비밀번호 없음

        user = userRepository.save(user);

        return new UserResponse(user.getUserId(), user.getUsername(), user.getEmail());
    }


    public String authenticate(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 잘못되었습니다."));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 잘못되었습니다.");
        }

        return user.getUsername(); // 또는 userId 등 토큰에 포함하고 싶은 값
    }

}

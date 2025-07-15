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

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse signup(UserSignupRequest request) {
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
        // 이미 가입된 사용자라면 조회
        Optional<User> userOpt = userRepository.findByProviderAndProviderId(provider, providerId);

        User user;
        if (userOpt.isPresent()) {
            user = userOpt.get();
        } else {
            // 새로 가입 처리
            user = new User();
            user.setProvider(provider);
            user.setProviderId(providerId);
            user.setEmail(email);
            user.setUsername(username != null ? username : email);
            user.setPassword(""); // 소셜 로그인 회원은 비밀번호 없음

            user = userRepository.save(user);
        }
        return new UserResponse(user.getUserId(), user.getUsername(), user.getEmail());
    }

}

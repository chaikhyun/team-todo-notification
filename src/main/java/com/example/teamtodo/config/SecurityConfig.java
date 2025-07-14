package com.example.teamtodo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        // 스웨거 및 리소스 접근 허용
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/openapi.yaml",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        // 로그인 페이지는 모두 접근 가능
                        .requestMatchers("/login").permitAll()
                        // 그 외 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                // 로그인 폼 설정
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                )
                .logout(logout -> logout.permitAll());

        return http.build();
    }
}

package com.example.teamtodo.jwt;

import com.example.teamtodo.exception.TokenLogoutException;
import com.example.teamtodo.security.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.data.redis.core.RedisTemplate;


import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;  // 추가!
    private final RedisTemplate<String, String> redisTemplate;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = jwtTokenProvider.resolveToken(request);

        // [1] 토큰이 아예 없으면 그냥 다음 필터로 넘김
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // [2] Redis에서 로그아웃 여부 확인
        String isLogout = redisTemplate.opsForValue().get(token);
        if ("logout".equals(isLogout)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            String json = """
        {
          "error": "UNAUTHORIZED",
          "message": "이미 로그아웃된 토큰입니다."
        }
        """;
            response.getWriter().write(json);
            return;
        }



        // [3] 인증이 필요 없는 경로는 예외 처리
        String path = request.getRequestURI();
        if (path.startsWith("/api/auth/login") ||
                path.startsWith("/api/auth/signup") ||
                path.startsWith("/api/auth/send-email") ||
                path.startsWith("/api/auth/verify-email") ||
                path.startsWith("/api/auth/refresh") ||
                path.startsWith("/api/auth/logout")) {
            filterChain.doFilter(request, response);
            return;
        }

        // [4] 유효한 토큰이면 인증 처리
        if (jwtTokenProvider.validateToken(token)) {
            String username = jwtTokenProvider.getUsernameFromToken(token);
            System.out.println("JWT 토큰에서 추출된 이메일(주체): " + username);

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            System.out.println("사용자 조회 성공: " + userDetails.getUsername());

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            System.out.println("토큰이 없거나 유효하지 않음");
        }


        filterChain.doFilter(request, response);
    }


}



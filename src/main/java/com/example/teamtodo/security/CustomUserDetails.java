package com.example.teamtodo.security;

import com.example.teamtodo.domain.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // 권한 처리 안할 거면 비워도 됨
    }

    @Override
    public String getPassword() {
        return user.getPassword(); // JWT 인증 시 사용하지 않음
    }

    @Override
    public String getUsername() {
        return user.getUsername(); // subject로 들어가는 값
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}

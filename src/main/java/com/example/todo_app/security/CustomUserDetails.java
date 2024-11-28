package com.example.todo_app.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;

// CustomUserDetails : Spring Security 의 UserDetails 인터페이스를 구현하는 클래스이며,
// User 엔티티(사용자 정보)를 래핑하여 Spring Security 가 인증 과정을 통해 사용자 정보를 관리하고 접근할 수 있도록 함
public class CustomUserDetails implements UserDetails {

    private final int userId;  // 필수 커스텀 필드
    private final String username;  // Spring Security 인증에 필요한 기본필드
    private final String password;  // Spring Security 인증에 필욯한 기본필드

    public CustomUserDetails(int userId, String username, String password) {
        this.userId = userId;
        this.username = username;
        this.password = password;
    }

    // 권한이 필요 없을 경우 빈 리스트 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    // 커스텀 메서드
    public int getUserId() {
        return userId;
    }

    // 인증을 위한 필수 메서드
    @Override
    public String getUsername() {
        return username;
    }

    // 인증을 위한 필수 메서드
    @Override
    public String getPassword() {
        return password;
    }

    // 계정 상태 정보
    // 계정 만료 여부를 반환. 기본적으로 true (만료되지 않음).
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정 잠금 여부를 반환. 기본적으로 true (잠기지 않음).
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 자격 증명(비밀번호) 만료 여부를 반환. 기본적으로 true (만료되지 않음).
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정 활성화 여부를 반환. 기본적으로 true (활성화 상태).
    @Override
    public boolean isEnabled() {
        return true;
    }
}

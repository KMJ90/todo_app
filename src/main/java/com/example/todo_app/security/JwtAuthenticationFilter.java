package com.example.todo_app.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

// JWT 인증 필터 : 요청마다 JWT 토큰을 검증하고 인증 정보를 설정
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JWTProvider jwtProvider;
    private final UserDetailsService userDetailsService;

    // 생성자 : JWTProvider 와 UserDetailsService 를 주입받아 초기화
    public JwtAuthenticationFilter(JWTProvider jwtProvider, UserDetailsService userDetailsService) {
        this.jwtProvider = jwtProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    // HTTP 요청마다 실행 : JWT 토큰 검증 및 인증 처리
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        // 요청에서 JWT 토큰 추출
        String token = resolveToken(request);

        // 토큰이 유효하면 사용자 정보 로드 및 인증 객체 설정
        if (token != null && jwtProvider.validateToken(token)) {
            // 토큰에서 사용자 ID 추출
            int userId = jwtProvider.getUserIdFromToken(token);

            // 사용자 ID로 UserDetails 조회
            UserDetails userDetails = userDetailsService.loadUserByUsername(String.valueOf(userId));

            if (userDetails != null) {
                // 인증 객체 생성 및 SecurityContext 에 설정
                var authentication = jwtProvider.getAuthentication (token, userDetails);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        // 다음 필터로 요청 전달
        chain.doFilter(request, response);
    }

    // 요청 헤더에서 JWT 토큰 추출
    private String resolveToken(HttpServletRequest request) {
         String bearerToken = request.getHeader("Authorization");
         if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
             return bearerToken.substring(7); // "Bearer" 접두사를 제거하고 토큰 반환
         }
         return null; // 토큰이 없으면 null 반환
    }
}

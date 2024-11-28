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

        String requestPath = request.getServletPath();
        logger.debug("요청 경로: " + requestPath);

        // 인증이 필요 없는 경로를 필터링
        if (requestPath.startsWith("/users/") &&
            (requestPath.endsWith("login") || requestPath.endsWith("register"))) {
            logger.debug("인증이 필요 없는 경로입니다. 필터를 건너뜁니다.");
            chain.doFilter(request, response);
            return;
        }

        // 요청 헤더에서 토큰 추출
        String token = resolveToken(request);
        logger.debug("추출된 토큰: " + token);

        if (token != null && jwtProvider.validateToken(token)) {
            logger.info("토큰이 유효합니다.");
            try {
                // 토큰에서 사용자 ID 추출 및 UserDetails 로드
                int userId = jwtProvider.getUserIdFromToken(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(String.valueOf(userId));
                logger.debug("로드된 사용자 정보: " + userDetails);

                // Authentication 객체 설정
                var authentication = jwtProvider.getAuthentication(userDetails);
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                logger.error("사용자 인증에 실패했습니다.", e);
            }
        } else {
            logger.warn("Authorization 헤더가 없거나 토큰 형식이 잘못되었습니다.");
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
         logger.warn("Authorization 헤더가 없거나 형식이 잘못되었습니다.");
         return null; // 토큰이 없으면 null 반환
    }
}

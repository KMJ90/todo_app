package com.example.todo_app.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JWTProvider {

    private static final Logger logger = LoggerFactory.getLogger(JWTProvider.class);

    // 비밀 키 (환경 변수(@Value)로 관리하는 것을 권장)
    @Value("${jwt.secret}")  // application.properties 의 jwt.secret 값 가져오기
    private String SECRET_KEY;

    private Key signingKey;

    // 토큰 유효 기간 : 1시간
    private static final long VALIDITY_IN_MILLISECONDS = 3600000;

    @PostConstruct
    public void init() {
        try {
            if (SECRET_KEY == null || SECRET_KEY.isBlank()) {
                throw new IllegalArgumentException("SECRET_KEY 값이 설정되지 않았습니다.");
            }
            // Base64 디코딩된 Secret Key 를 Key 객체로 변환
            signingKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET_KEY));
            logger.info("JWTProvider 초기화 완료: SECRET_KEY 가 설정되었습니다.");
        } catch (IllegalArgumentException e) {
            logger.error("SECRET_KEY 가 유효하지 않은 Base64 문자열입니다.", e);
            throw e; // 애플리케이션 초기화 실패
        }
    }

    // JWT 토큰 생성
    public String generateToken(int userId) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + VALIDITY_IN_MILLISECONDS);
        logger.info("Generating token for user ID: {}", userId);
        logger.info("Token validity: {}", validity);

        return Jwts.builder()
                .setSubject("auth-token")
                .claim("userId", userId) // 사용자 ID를 클레임으로 저장
                .setIssuedAt(now)  // 토큰 생성 시간
                .setExpiration(validity)  // 토큰 만료 시간 (1시간 유효)
                .signWith(signingKey, SignatureAlgorithm.HS256)  // 서명
                .compact();
    }

    // JWT 토큰 검증
    public boolean validateToken(String token) {
        try {
            // 토큰 파싱 및 서명 검증
            Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token);
            return true;  // 유효한 토큰
        } catch (io.jsonwebtoken.security.SecurityException e) {
            logger.error("서명이 유효하지 않은 JWT 토큰", e);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            logger.error("만료된 JWT 토큰", e);
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            logger.error("형식이 올바르지 않은 JWT 토큰", e);
        } catch (Exception e) {
            logger.error("JWT 토큰 처리 중 알 수 없는 오류", e);
        }
        return false;  // 유효하지 않은 토큰
    }

    // 사용자 ID를 클레임에서 직접 추출하며, Integer 타입으로 반환
    public int getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("userId", Integer.class);  // 사용자 ID를 클레임에서 직접 가져오기
        } catch (Exception e) {
            logger.error("JWT 토큰에서 사용자 ID 를 추출하는 중 오류가 발생했습니다.", e);
            throw new RuntimeException("토큰에서 사용자 ID 를 추출할 수 없습니다.");
        }
    }

    // getAuthentication 메서드
    public Authentication getAuthentication(UserDetails userDetails) {
        return new UsernamePasswordAuthenticationToken(
                userDetails, // 사용자 정보
                null,  // 인증 정보 (비밀번호 등, 필요 업으므로 null)
                userDetails.getAuthorities()  // 사용자 권한 목록
        );
    }
}
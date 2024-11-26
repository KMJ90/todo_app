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
        // Base64 디코딩된 byte[]를 Key 로 변환
        signingKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET_KEY));
    }

    // JWT 토큰 생성
    public String generateToken(int userId) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + VALIDITY_IN_MILLISECONDS);

        return Jwts.builder()
                .setSubject(String.valueOf(userId)) // 사용자 ID를 토큰에 포함
                .setIssuedAt(now)  // 토큰 생성 시간
                .setExpiration(validity)  // 토큰 만료 시간 (1시간 유효)
                .signWith(signingKey, SignatureAlgorithm.HS256)  // Key 객체 사용
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
        return false;
    }

    // JWT 토큰에서 사용자 ID 추출
    public int getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Integer.parseInt(claims.getSubject());
    }

    // getAuthentication 메서드
    public Authentication getAuthentication(String token, UserDetails userDetails) {
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}

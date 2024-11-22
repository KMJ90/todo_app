package com.example.todo_app.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.util.Base64;
import java.util.Date;

@Component
public class JWTProvider {

    private static final Logger logger = LoggerFactory.getLogger(JWTProvider.class);

    // 비밀 키 (환경 변수(@Value)로 관리하는 것을 권장)
    @Value("${jwt.secret}")  // application.properties 의 jwt.secret 값 가져오기
    private String SECRET_KEY;

    // 토큰 유효 기간 : 1시간
    private static final long VALIDITY_IN_MILLISECONDS = 3600000;

    @PostConstruct
    public void init() {
        // Base64 디코딩된 SECRET_KEY 설정
        SECRET_KEY = new String(Base64.getDecoder().decode(SECRET_KEY));
    }

    // JWT 토큰 생성
    public String generateToken(int userId) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + VALIDITY_IN_MILLISECONDS);

        return Jwts.builder()
                .setSubject(String.valueOf(userId)) // 사용자 ID를 토큰에 포함
                .setIssuedAt(now)  // 토큰 생성 시간
                .setExpiration(validity)  // 토큰 만료 시간 (1시간 유효)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)  // 시크릿 키로 서명
                .compact();
    }

    // JWT 토큰 검증
    public boolean validateToken(String token) {
        try {
            // 토큰 파싱 및 서명 검증
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;  // 유효한 토큰
        } catch(Exception e) {
            return false;  // 유효하지 않은 토큰
        }
    }

    // JWT 토큰에서 사용자 ID 추출
    public int getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
        return Integer.parseInt(claims.getSubject());
    }

    // getAuthentication 메서드
    public Authentication getAuthentication(String token, UserDetails userDetails) {
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}

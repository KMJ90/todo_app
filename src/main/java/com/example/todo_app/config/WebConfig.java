package com.example.todo_app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// 백엔드 포트 : localhost:8080
// 프론트엔드 포트 : localhost:3000
// 으로 포트가 다르기 때문에, 클라이언트가 서버에 요청을 할 수 없음
// 요청을 시도 한다면, CORS 정책 위반

// CORS 정책에 대한 모든 허용 설정
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // 모든 경로에 대해
                .allowedOrigins("http://localhost:3000")  // 허용할 도메인 (프론트엔드 URL)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 필요한 메서드를 허용
                .allowedHeaders("*")  // 모든 헤더 허용
                .allowCredentials(true);  // 인증 정보 허용 (쿠키 등)
    }
}

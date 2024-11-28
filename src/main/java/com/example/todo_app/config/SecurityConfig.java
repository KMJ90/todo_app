package com.example.todo_app.config;

import com.example.todo_app.security.JWTProvider;
import com.example.todo_app.security.JwtAuthenticationFilter;
import com.example.todo_app.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity  // Spring Security 를 활성화하고, 이 클래스가 웹 보안 설정을 위한 것임을 지정
public class SecurityConfig {

    // CustomUserDetailsService : 사용자 인증 정보를 로드
    private final CustomUserDetailsService customUserDetailsService;
    private final JWTProvider jwtProvider;

    // CustomUserDetailsService 를 주입받아 인증 설정에 사용
    public SecurityConfig(CustomUserDetailsService customUserDetailsService, JWTProvider jwtProvider) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtProvider = jwtProvider;
    }

    @Bean
    // passwordEncoder : 비밀번호를 암호화하여 저장하고 비교할때 사용
    public PasswordEncoder passwordEncoder() {
        // BCrypt 암호화 알고리즘을 사용하여 비밀번호를 안전하게 처리
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtProvider, customUserDetailsService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CSRF(Cross-Site Request Forgery) JWT 사용시 일반적으로 필요 없음
            .csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화

            // H2 관련 헤더 설정 (개발 환경에서만 허용)
            .headers(headers -> headers
                // frameOptions: 동일한 출처에서만 페이지 내에서 <iframe>을 사용할 수 있도록 설정. H2 콘솔을 <iframe>으로 열 수 있게 해주는 설정
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin) // H2 콘솔을 위한 프레임 옵션 허용
            )
            // 요청 경로별 접근 권한 설정
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/h2-console/**").permitAll() // 이 경로는 인증 없이 접근 가능
                .requestMatchers("/users/register", "/users/login", "/todos/categories").permitAll() // 이 경로는 인증 없이 접근 가능
                .requestMatchers("/users/me").authenticated() // 인증 필요
                .anyRequest().authenticated() // 그 외 요청은 인증 필요
            )

            // JWT 인증 필터 추가
            .addFilterBefore(jwtAuthenticationFilter(),
                UsernamePasswordAuthenticationFilter.class) // 필터 등록

            // logout : 로그아웃 관련 설정
            .logout(logout -> logout
                    // logoutUrl("/logout"): 로그아웃 경로를 /logout 으로 설정
                    .logoutUrl("/logout")
                    .logoutSuccessHandler((request, response, authentication) -> {
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.getWriter().write("로그아웃 성공");
                    })
            );
        return http.build();
    }

    @Bean
    // AuthenticationManager: Spring Security 에서 인증을 관리하는 핵심 컴포넌트
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder auth = http.getSharedObject(AuthenticationManagerBuilder.class);
        // userDetailsService(customUserDetailsService)와 passwordEncoder(passwordEncoder())를 사용하여 인증에 필요한 사용자 정보를 로드하고,
        // 비밀번호를 검증하는 과정을 설정
        auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());
        return auth.build();
    }
}

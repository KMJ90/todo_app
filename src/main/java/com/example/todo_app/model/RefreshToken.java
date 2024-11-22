package com.example.todo_app.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// JWT 인증 토큰 관리
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String token;  // 리프레시 토큰

    // User 한명이 여러 RefreshToken(로그인을 할 때 발급되는 임시인증) 을 가질 수 있음
    // (테블릿,휴대폰,컴퓨터 등 여러기기에서 로그인 할 수 있기 때문에, 유저 한명당 토큰이 여러가 가능)
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // 토큰이 속한 사용자

    @Column(nullable = false)
    private LocalDateTime expiryDate;  // 토큰 만료 시간

    public RefreshToken() {}

    public RefreshToken(int id, String token, User user, LocalDateTime expiryDate) {
        this.id = id;
        this.token = token;
        this.user = user;
        this.expiryDate = expiryDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    @Override
    public String toString() {
        return "RefreshToken{" +
                "id=" + id +
                ", token='" + token + '\'' +
                ", user=" + user +
                ", expiryDate=" + expiryDate +
                '}';
    }
}

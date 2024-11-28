package com.example.todo_app.service;

import com.example.todo_app.repository.UserRepository;
import com.example.todo_app.security.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.example.todo_app.model.User;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    public final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        try {
            int id = Integer.parseInt(userId); // String(userId) 을 int 로 변환
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + id));

            // Spring Security 와 통합하기 위해 CustomUserDetails 객체를 반환
            // 이는 User 객체의 데이터를 Spring Security 가 사용할 수 있는 형식으로 변환
            return new CustomUserDetails(user.getId(), user.getUsername(), user.getPassword());
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("유효하지 않은 사용자 ID: " + userId);
        }
    }
}

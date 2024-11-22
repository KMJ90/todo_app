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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // userRepository 를 통해 username 으로 User 엔티티 조회
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        // CustomUserDetails 객체를 생성할 때 필요한 값 (userId, username, password) 전달
        return new CustomUserDetails(
                user.getId(),  // userId
                user.getUsername(),  // username
                user.getPassword()  // password
        );
    }
}

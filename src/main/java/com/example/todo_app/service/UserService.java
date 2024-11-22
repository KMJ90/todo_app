package com.example.todo_app.service;

import com.example.todo_app.dto.TodoDTO;
import com.example.todo_app.dto.UserDTO;
import com.example.todo_app.model.User;
import com.example.todo_app.repository.UserRepository;
import com.example.todo_app.security.JWTProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service  // 이 클래스가 Spring 의 서비스 계층으로 동작하도록 지정
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTProvider jwtProvider;

    @Autowired  // 생성자 주입으로 UserRepository, PasswordEncoder 를 주입
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JWTProvider jwtProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    // 회원가입 메서드 - 사용자 이름 중복 검사 및 비밀번호 암호화 후 저장
    public void registerUser(String username, String password) {
        // 사용자 이름이 이미 존재하는지 확인
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자 이름입니다.");
        }
        // 새로운 사용자 생성 및 저장
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password)); // 비밀번호 암호화
        userRepository.save(newUser);
    }

    // 로그인 메서드 - 사용자 이름과 비밀번호 검증 후 DTO 반환
    public Optional<String> loginUser(String username, String password) {
        // 사용자 이름으로 DB 에서 User 객체 찾기
        Optional<User> userOptional = userRepository.findByUsername(username);

       if (userOptional.isPresent()) {
           User user = userOptional.get();
           logger.info("사용자 찾음: {}", username);

           // 비밀번호 비교
           if (passwordEncoder.matches(password, user.getPassword())) {
               logger.info("비밀번호 일치: {}", username);

               // JWT 토큰 생성
               String jwtToken = jwtProvider.generateToken(user.getId());
               logger.info("JWT 생성 완료: {}", jwtToken);
               return Optional.of(jwtToken);  // JWT 토큰 반환
           } else {
               // 비밀번호 불일치 로그 남기기
               logger.warn("로그인 실패: 사용자 {}(이)가 잘못된 비밀번호를 입력", username);
           }
       } else {
           // 사용자 이름으로 찾지 못한 경우 로그 남기기
           logger.warn("로그인 실패: 사용자 이름 {}(이)가 존재하지 않음", username);
       }
        return Optional.empty();  // 로그인 실패 시 빈 Optional 반환
    }

    // 현재 로그인한 사용자 정보 조회
    public UserDTO getCurrentUser() {
        // SecurityContext 이용해 현재 로그인된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // 현재 로그인한 사용자의 username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("현재 사용자를 찾을 수 없습니다."));
        return convertToDTO(user); // User 엔티티를 DTO 로 변환하여 반환
    }

    // User 엔티티를 UserDTO 로 변환
    public UserDTO convertToDTO(User user) {
        List<TodoDTO> todoDTOs = user.getTodos()
                .stream()
                .map(todo -> new TodoDTO(todo.getId(),
                        todo.getTitle(),
                        todo.getCompleted(),
                        todo.getDueDate(),
                        todo.getPriority(),
                        todo.getPosition(),
                        todo.getTags(),
                        todo.getCreatedAt(),
                        todo.getUpdatedAt(),
                        todo.getUser().getId()))
                .collect(Collectors.toList());
        return new UserDTO(user.getId(), user.getUsername(), todoDTOs);
    }
}

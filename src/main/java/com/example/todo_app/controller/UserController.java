package com.example.todo_app.controller;

import com.example.todo_app.dto.LoginRequestDTO;
import com.example.todo_app.dto.RegisterRequestDTO;
import com.example.todo_app.dto.UserDTO;
import com.example.todo_app.model.User;
import com.example.todo_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController  // Restfull 웹 서비스의 컨트롤러로 사용
@RequestMapping("/users")  // 모든 경로가 "/users"로 시작
public class UserController {

    private final UserService userService;

    @Autowired  // 생성자 주입으로 UserService 를 주입
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 회원가입 엔드포인트
    // @Valid : User 엔티티를 받을 때, 추가적인 유효성 검증(예: 사용자 이름 및 비밀번호 길이)을 설정하면 좋음
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequestDTO request) {
        try {
            // UserService 를 사용해 회원가입 처리
            userService.registerUser(request.getUsername(), request.getPassword());
            return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 성공");
        } catch (IllegalArgumentException e) {
            // 유효성 검사 실패 시 에러 메시지 반환
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 로그인 엔드포인드
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginRequestDTO request) {
        // UserService 를 통해 사용자 인증 후 JWT 토큰 반환
        Optional<String> jwtToken = userService.loginUser(request.getUsername(), request.getPassword());
        return jwtToken.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패 : 사용자 이름 또는 비밀번호가 잘못되었습니다."));
    }

    // 로그아웃 엔드포인트
    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser() {
        // 클라이언트가 로그아웃 성공 메시지를 받으면, 클라이언트 측에서 토큰을 폐기
        return ResponseEntity.ok("로그아웃 성공: 클라이언트에서 토큰을 삭제하세요.");
    }

    // 현재 로그인한 사용자 정보 조회 엔드포인트
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser() {
        UserDTO currentUser = userService.getCurrentUser();  // Service 계층에서 SecurityContext 를 활용
        return ResponseEntity.ok(currentUser);
    }
}

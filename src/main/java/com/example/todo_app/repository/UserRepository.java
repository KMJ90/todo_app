package com.example.todo_app.repository;

import com.example.todo_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    //  Spring Data JPA 의 JpaRepository<User, Integer>에서 기본적인 CRUD 메소드를 제공하기 때문에, 작성하지 않음
    //  Spring Data JPA 에서 (사용자 이름으로 조회) 기능은 제공하지 않기 때문에 커스텀 메소드로 정의한 것임
    Optional<User> findByUsername(String username);  // 사용자 이름으로 사용자 찾기
}

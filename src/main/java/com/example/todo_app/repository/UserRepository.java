package com.example.todo_app.repository;

import com.example.todo_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    // Spring Data JPA 의 JpaRepository<User, Integer>에서 기본적인 CRUD 메소드를 제공하기 때문에, 작성하지 않음
    Optional<User> findByUsername(String username); // 사용자 이름으로 조회

    // `join fetch`로 todos 를 한 번에 로드
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.todos WHERE u.username  = :username ")
    Optional<User> findByUsernameWithTodos(@Param("username") String username);

}

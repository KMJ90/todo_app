package com.example.todo_app.repository;

import com.example.todo_app.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Integer> {
    // Spring Data JPA 에서 기본적인 CRUD 를 제공하지만 ~~
    // 예시
    // 전체조회 -> 모든 엔티티를 조회
    // 특정조회 -> 주어진 ID로 특정 엔티티를 조회
    // 생성 및 수정 -> 새로운 엔티티를 저장하거나, 기존 엔티티를 수정

    // 완료되지 않은 및 완료된 등 특정 조건에 따라 데이터를 필터링하는 기능은 제공하지 않기 때문에
    // 커스텀 메소드로 정의해야 한다


    // 카테고리별 Id를 기준으로 모든 할 일 목록 조회 및 정렬
    List<Todo> findByCategoryIdOrderByPositionAsc(int categoryId);

    // 카테고리 Id와 할 일 Id로 특정 단일 할 일 조회
    Optional<Todo> findByCategoryIdAndId(int categoryId, int id);

    // 특정 카테고리에 속하고 완료되지 않은 할 일 목록 조회 및 정렬
    List<Todo> findByCategoryIdAndCompletedFalseOrderByPositionAsc(int categoryId);

    // 특정 카테고리에 속하고 완료된 할 일 목록 조회 및 정렬
    List<Todo> findByCategoryIdAndCompletedTrueOrderByPositionAsc(int categoryId);
}

package com.example.todo_app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// 할 일 관리
@Entity  // 이 클래스가 JPA 엔티티 임을 명시
@Table(name = "todos")  // 테이블 이름을 todos 로 지정하고 매핑
public class Todo {

    @Id  // 해당 필드가 기본 key 임을 명시
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // id 필드가 자동 증가되도록 설정
    private int id;

    @Column(nullable = false)
    private String title;  // 할 일 제목

    @Column(nullable = false)
    private Boolean completed = false;  // 할 일 완료 여부

    @Column
    private LocalDateTime dueDate;  // 마감 기한

    @Column(nullable = true) // nullable 을 true 로 설정하여 필수 입력이 아니도록 함(우선순위가 변경되면, 업데이트 되도록 함)
    @Min(1)  // 최소값 1
    @Max(3)  // 최대값 3
    private int priority = 1;  // 할 일의 우선 순위

    @Column(nullable = true)
    private Integer position; // 할 일의 리스트 내 순서를 나타내는 필드

    @ElementCollection(fetch = FetchType.EAGER)  // 기본 데이터 타입이나 Embeddable 타입을 엔티티에 포함시키고, 데이터베이스에서 별도의 테이블로 관리하도록 JPA 에 알려줌
    @CollectionTable(name = "todo_tags", joinColumns = @JoinColumn(name = "todo_id"))  // todo_id 라는 외래키 설정 -> todo_tags 테이블은 어떤 to_do 항목에 해당 태그들이 연결되어 있는지 알기 위해 필요
    @Column(name = "tag")  // todo_tags 테이블에 태그가 저장 될 때, tag 컬럼에 저장되도록 설정
    private List<String> tags = new ArrayList<>();  // 태그 리스트 - 빈 리스트로 초기화

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;  // 생성 시간

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;  // 수정 시간

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // To-Do 항목이 속한 사용자

    // optional = false : 연관된 엔티티가 존재하지 않으면 해당 엔티티를 저장할 수 없도록 제한
    // 관계 자체의 필수 여부를 의미 (예를 들어, To-do 엔티티에 Category 가 반드시 존재해야 한다는 것을 JPA 에 알려줌)
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    // nullable = false : 데이터베이스 레벨에서 필드가 null 값을 가질 수 없도록 제약
    // 데이터베이스에 실제로 생성된 테이블에서 category_id 컬럼이 null 값을 가지지 않도록 제약 설정
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;  // 카테고리와의 관계

    // 기본 생성자
    public Todo() {}

    // 모든 필드 생성자

    public Todo(int id, String title, Boolean completed, LocalDateTime dueDate, int priority, Integer position, List<String> tags, LocalDateTime createdAt, LocalDateTime updatedAt, User user, Category category) {
        this.id = id;
        this.title = title;
        this.completed = completed;
        this.dueDate = dueDate;
        this.priority = priority;
        this.position = position;
        this.tags = tags;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.user = user;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Todo{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", completed=" + completed +
                ", dueDate=" + dueDate +
                ", priority=" + priority +
                ", position=" + position +
                ", tags=" + tags +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", user=" + user +
                ", category=" + category +
                '}';
    }
}

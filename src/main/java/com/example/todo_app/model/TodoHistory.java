package com.example.todo_app.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

// 상태 변화 기록
@Entity
@Table(name = "todo_history")
public class TodoHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "todo_id", nullable = false)
    private Todo todo;  // 기록할 할 일

    @Column(nullable = false)
    private String changeDescription;  // 변경 사항 설명

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime timestamp;  // 변경 시간

    public TodoHistory() {}

    public TodoHistory(int id, Todo todo, String changeDescription, LocalDateTime timestamp) {
        this.id = id;
        this.todo = todo;
        this.changeDescription = changeDescription;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Todo getTodo() {
        return todo;
    }

    public void setTodo(Todo todo) {
        this.todo = todo;
    }

    public String getChangeDescription() {
        return changeDescription;
    }

    public void setChangeDescription(String changeDescription) {
        this.changeDescription = changeDescription;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "TodoHistory{" +
                "id=" + id +
                ", todo=" + todo +
                ", changeDescription='" + changeDescription + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}

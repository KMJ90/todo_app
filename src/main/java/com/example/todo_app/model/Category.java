package com.example.todo_app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

// 카테고리 관리
@Entity
@Table(name = "categories")
public class Category {

    @Id
    private int id;

    @Column(nullable = false)
    private String name;  // 카테고리 이름 (예: 업무, 개인, 여행)

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Todo> todos;  // 해당 카테고리에 속한 할 일 목록

    public Category() {}

    public Category(int id, String name, List<Todo> todos) {
        this.id = id;
        this.name = name;
        this.todos = todos;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Todo> getTodos() {
        return todos;
    }

    public void setTodos(List<Todo> todos) {
        this.todos = todos;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", todos=" + todos +
                '}';
    }
}

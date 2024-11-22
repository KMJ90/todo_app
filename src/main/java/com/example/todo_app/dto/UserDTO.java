package com.example.todo_app.dto;

import java.util.List;

public class UserDTO {

    // 보안상의 이유로 유저 비밀번호와 같은 민감정보는 DTO 필드로 넣지않음
    // 비밀번호 조회가 필요시 Repository 를 이용해 <User> 엔티티를 불러와서 조회함
    private int id;
    private String username;
    private List<TodoDTO> todos;

    public UserDTO(int id, String username, List<TodoDTO> todos) {
        this.id = id;
        this.username = username;
        this.todos = todos;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<TodoDTO> getTodos() {
        return todos;
    }

    public void setTodos(List<TodoDTO> todos) {
        this.todos = todos;
    }
}

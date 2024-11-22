package com.example.todo_app.controller;

import com.example.todo_app.dto.TodoDTO;
import com.example.todo_app.model.Category;
import com.example.todo_app.model.Todo;
import com.example.todo_app.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/todos")
public class TodoController {

    private final TodoService todoService;

    @Autowired  // 생성자 주입으로 TodoService 와 CategoryService 를 주입
    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    // 할 일 생성
    @PostMapping("/categories/{categoryId}/create")
    public ResponseEntity<TodoDTO> createTodo(@PathVariable int categoryId, @RequestBody TodoDTO todoDTO) {
        TodoDTO createdTodo = todoService.createTodoWithCategory(categoryId, todoDTO);
        return new ResponseEntity<>(createdTodo, HttpStatus.CREATED);
    }

    // 모든 카테고리 조회
    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = todoService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    // 카테고리별 할 일 조회 (카테고리 클릭 시 할 일 목록을 보여줌)
    @GetMapping("/categories/{categoryId}")
    public ResponseEntity<List<TodoDTO>> getTodosByCategory(@PathVariable int categoryId) {
        List<TodoDTO> todosByCategory = todoService.getTodosByCategory(categoryId);
        return ResponseEntity.ok(todosByCategory);
    }

    // 카테고리 내 완료되지 않은 할 일 조회
    @GetMapping("/categories/{categoryId}/incomplete")
    public ResponseEntity<List<TodoDTO>> getIncompleteTodosByCategory(@PathVariable int categoryId) {
        List<TodoDTO> incompleteTodos = todoService.getIncompleteTodosByCategory(categoryId);
        return ResponseEntity.ok(incompleteTodos);
    }

    // 카테고리 내 완료된 할 일 조회
    @GetMapping("/categories/{categoryId}/completed")
    public ResponseEntity<List<TodoDTO>> getCompletedTodosByCategory(@PathVariable int categoryId) {
        List<TodoDTO> completedTodos = todoService.getCompletedTodosByCategory(categoryId);
        return ResponseEntity.ok(completedTodos);
    }

    // 카테고리별 할 일 일괄 업데이트
    @PutMapping("/categories/{categoryId}")
    public ResponseEntity<TodoDTO> updateTodoByCategory(@PathVariable int categoryId, @RequestBody TodoDTO todo) {
        TodoDTO updatedTodo = todoService.updateTodoByCategory(categoryId, todo);
        return ResponseEntity.ok(updatedTodo);
    }

    // 드래그 앤 드롭을 통해 우선순위 업데이트
    @PutMapping("/reorder")
    public ResponseEntity<List<TodoDTO>> reorderTodos(@RequestBody List<Integer> orderedTodoIds) {
        List<TodoDTO> updatedTodos = todoService.updateTodoPositions(orderedTodoIds);
        return ResponseEntity.ok(updatedTodos);
    }

    // 단일 할 일 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodoById(@PathVariable int id) {
        // 단일 ID로 할 일 삭제
        todoService.deleteTodoById(id);
        return ResponseEntity.noContent().build();
    }
}

package com.example.todo_app.service;

import com.example.todo_app.dto.TodoDTO;
import com.example.todo_app.model.Category;
import com.example.todo_app.model.Todo;
import com.example.todo_app.model.User;
import com.example.todo_app.repository.CategoryRepository;
import com.example.todo_app.repository.TodoRepository;
import com.example.todo_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

// 조회와 (생성,수정)이 메소드 형태가 다른 이유
// [조회]는 해당하는 ID를 조회를 했을 때 데이터가 없어서, null 이 나올 수 있기 때문에 -> NullPointerException 발생
// 이를 해결하기 위해 Optional<T>을 사용해서, null 일 경우 Optional.empty() 반환

// [생성과 수정]은 작업이 성공적으로 완료되었을 때 결과물이 항상 존재하므로, Optional 로 감싸지 않고 그 자체를 반환

@Service
public class TodoService {

    private final TodoRepository todoRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    // 생성자를 통해 TodoRepository 를 주입받음 (의존성 주입)
    @Autowired
    public TodoService(TodoRepository todoRepository, CategoryRepository categoryRepository, UserRepository userRepository) {
        this.todoRepository = todoRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    // To-do 엔티티를 TodoDTO 로 변환
    private TodoDTO convertToDTO(Todo todo) {
        return new TodoDTO(todo.getId(), todo.getTitle(), todo.getCompleted(), todo.getDueDate(), todo.getPriority(), todo.getPosition(), todo.getTags(), todo.getCreatedAt(), todo.getUpdatedAt(), todo.getUser().getId());
    }

    // TodoDTO 를 To-do 엔티티로 변환
    private Todo convertToEntity(TodoDTO todoDTO, Category category) {
        Todo todo = new Todo();
        todo.setId(todoDTO.getId());
        todo.setTitle(todoDTO.getTitle());
        todo.setCompleted(todoDTO.getCompleted());
        todo.setDueDate(todoDTO.getDueDate());
        todo.setPriority(todoDTO.getPriority() == 0 ? 1 : todoDTO.getPriority());
        todo.setPosition(todoDTO.getPosition());
        todo.setTags(todoDTO.getTags());
        todo.setCategory(category);

        return todo;
    }

    // 카테고리 ID를 기반으로 할 일 생성
    public TodoDTO createTodoWithCategory(int categoryId, TodoDTO todoDTO) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("카테고리를 찾을 수 없습니다: " + categoryId));

        User user = userRepository.findById(todoDTO.getUserId()) // userId 로 사용자 조회
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + todoDTO.getUserId()));

        Todo todo = convertToEntity(todoDTO, category);
        todo.setUser(user);  // 할 일에 사용자 설정
        Todo savedTodo = todoRepository.save(todo);

        return convertToDTO(savedTodo);
    }

    // 모든 카테고리 조회
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // 카테고리별 할 일 조회 (position 기준 정렬)
    public List<TodoDTO> getTodosByCategory(int categoryId) {
        return todoRepository.findByCategoryIdOrderByPositionAsc(categoryId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 특정 카테고리에서 완료되지 않은 할 일 조회 (position 기준 정렬)
    public List<TodoDTO> getIncompleteTodosByCategory(int categoryId) {
        return todoRepository.findByCategoryIdAndCompletedFalseOrderByPositionAsc(categoryId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 특정 카테고리에서 완료된 할 일 조회 (position 기준 정렬)
    public List<TodoDTO> getCompletedTodosByCategory(int categoryId) {
        return todoRepository.findByCategoryIdAndCompletedTrueOrderByPositionAsc(categoryId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 카테고리별 할 일 일괄 업데이트
    public TodoDTO updateTodoByCategory(int categoryId, TodoDTO updatedTodo) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("카테고리를 찾을 수 없습니다: " + categoryId));

        // 카테고리 ID에 속하는 기존의 할 일 목록(existingTodo)을 데이터베이스에서 가져옴
        Todo existingTodo = todoRepository.findByCategoryIdAndId(categoryId, updatedTodo.getId())
                .orElseThrow(() -> new RuntimeException("해당 할 일을 찾을 수 없습니다: " + updatedTodo.getId()));

        // existingTodo : 데이터베이스에서 가져온 기존 할 일 목록
        // existingTodo 객체를 통해 업데이트 된 변경 사항은 existingTodos 에도 반영됨
        existingTodo.setTitle(updatedTodo.getTitle());
        existingTodo.setCompleted(updatedTodo.getCompleted());
        existingTodo.setDueDate(updatedTodo.getDueDate());
        existingTodo.setPriority(updatedTodo.getPriority());
        existingTodo.setPosition(updatedTodo.getPosition());
        existingTodo.setTags(updatedTodo.getTags());

        Todo savedTodo = todoRepository.save(existingTodo);
        return convertToDTO(savedTodo);
    }

    // 우선순위 업데이트 메소드
    public List<TodoDTO> updateTodoPositions(List<Integer> orderedTodoIds) {
        List<Todo> todos = todoRepository.findAllById(orderedTodoIds);

        for (int i = 0; i < orderedTodoIds.size(); i++) {
            int todoId = orderedTodoIds.get(i);
            Todo todo = todos.stream()
                    .filter(t -> t.getId() == todoId)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("할 일의 해당 ID를 찾을 수 없습니다 : " + todoId));
            todo.setPosition(i + 1);  // 순서대로 position 설정 (1, 2, 3, ...)
        }
        return todoRepository.saveAll(todos)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 개별 할 일 삭제 메서드
    public void deleteTodoById(int id) {
        if (todoRepository.existsById(id)) {
            todoRepository.deleteById(id);
        } else {
            throw new RuntimeException("할일의 ID " + id + "을(를) 찾을 수 없습니다");
        }
    }
}

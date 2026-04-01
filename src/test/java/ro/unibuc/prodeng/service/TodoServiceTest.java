package ro.unibuc.prodeng.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ro.unibuc.prodeng.exception.EntityNotFoundException;
import ro.unibuc.prodeng.model.TodoEntity;
import ro.unibuc.prodeng.model.UserEntity;
import ro.unibuc.prodeng.repository.TodoRepository;
import ro.unibuc.prodeng.request.AssignTodoRequest;
import ro.unibuc.prodeng.request.CreateTodoRequest;
import ro.unibuc.prodeng.request.EditTodoRequest;
import ro.unibuc.prodeng.response.TodoResponse;

@ExtendWith(MockitoExtension.class)
public class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TodoService todoService;

    @Test
    void testGetTodosByUserEmail() throws EntityNotFoundException {
        UserEntity user = new UserEntity("u1", "Alice", "alice@test.com");
        when(userService.getUserEntityByEmail("alice@test.com")).thenReturn(user);
        when(todoRepository.findByAssignedUserId("u1")).thenReturn(List.of(
            new TodoEntity("t1", "Buy milk", false, "u1")
        ));

        List<TodoResponse> res = todoService.getTodosByUserEmail("alice@test.com");
        assertEquals(1, res.size());
        assertEquals("Buy milk", res.get(0).description());
    }

    @Test
    void testGetTodoById() throws EntityNotFoundException {
        TodoEntity todo = new TodoEntity("t1", "Buy milk", false, "u1");
        UserEntity user = new UserEntity("u1", "Alice", "alice@test.com");
        when(todoRepository.findById("t1")).thenReturn(Optional.of(todo));
        when(userService.getUserEntityById("u1")).thenReturn(user);

        TodoResponse res = todoService.getTodoById("t1");
        assertEquals("Buy milk", res.description());
        assertEquals("Alice", res.assigneeName());
    }

    @Test
    void testCreateTodo() throws EntityNotFoundException {
        CreateTodoRequest req = new CreateTodoRequest("Buy milk", "alice@test.com");
        UserEntity user = new UserEntity("u1", "Alice", "alice@test.com");
        when(userService.getUserEntityByEmail("alice@test.com")).thenReturn(user);
        when(todoRepository.save(any())).thenAnswer(i -> {
            TodoEntity t = i.getArgument(0);
            return new TodoEntity("t1", t.description(), t.done(), t.assignedUserId());
        });

        TodoResponse res = todoService.createTodo(req);
        assertEquals("Buy milk", res.description());
        assertFalse(res.done());
        assertEquals("Alice", res.assigneeName());
    }

    @Test
    void testSetDone() throws EntityNotFoundException {
        TodoEntity existing = new TodoEntity("t1", "Buy milk", false, "u1");
        UserEntity user = new UserEntity("u1", "Alice", "alice@test.com");
        when(todoRepository.findById("t1")).thenReturn(Optional.of(existing));
        when(todoRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(userService.getUserEntityById("u1")).thenReturn(user);

        TodoResponse res = todoService.setDone("t1", true);
        assertTrue(res.done());
    }

    @Test
    void testAssign() throws EntityNotFoundException {
        TodoEntity existing = new TodoEntity("t1", "Buy milk", false, "u1");
        UserEntity newUser = new UserEntity("u2", "Bob", "bob@test.com");
        when(todoRepository.findById("t1")).thenReturn(Optional.of(existing));
        when(userService.getUserEntityByEmail("bob@test.com")).thenReturn(newUser);
        when(todoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        AssignTodoRequest req = new AssignTodoRequest("bob@test.com");
        TodoResponse res = todoService.assign("t1", req);
        assertEquals("Bob", res.assigneeName());
    }

    @Test
    void testEdit() throws EntityNotFoundException {
        TodoEntity existing = new TodoEntity("t1", "Buy milk", false, "u1");
        UserEntity user = new UserEntity("u1", "Alice", "alice@test.com");
        when(todoRepository.findById("t1")).thenReturn(Optional.of(existing));
        when(todoRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(userService.getUserEntityById("u1")).thenReturn(user);

        EditTodoRequest req = new EditTodoRequest("Buy water");
        TodoResponse res = todoService.edit("t1", req);
        assertEquals("Buy water", res.description());
    }

    @Test
    void testDeleteTodo() throws EntityNotFoundException {
        when(todoRepository.existsById("t1")).thenReturn(true);
        todoService.deleteTodo("t1");
        verify(todoRepository).deleteById("t1");
    }

    @Test
    void testDeleteTodoNotFound() {
        when(todoRepository.existsById("t2")).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> todoService.deleteTodo("t2"));
    }

    @Test
    void testGetEntityByIdNotFound() {
        when(todoRepository.findById("t2")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> todoService.getTodoById("t2"));
    }
}

package ro.unibuc.prodeng.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import ro.unibuc.prodeng.request.AssignTodoRequest;
import ro.unibuc.prodeng.request.CreateTodoRequest;
import ro.unibuc.prodeng.request.EditTodoRequest;
import ro.unibuc.prodeng.response.TodoResponse;
import ro.unibuc.prodeng.service.TodoService;
import ro.unibuc.prodeng.service.MasinaService;
import ro.unibuc.prodeng.service.UserService;
import ro.unibuc.prodeng.repository.UserRepository;
import ro.unibuc.prodeng.repository.MasinaRepository;
import ro.unibuc.prodeng.repository.TodoRepository;
import org.springframework.data.mongodb.core.MongoTemplate;

@WebMvcTest(TodoController.class)
public class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoService todoService;
    
    @MockBean
    private MasinaService masinaService;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private MasinaRepository masinaRepository;

    @MockBean
    private TodoRepository todoRepository;

    @MockBean
    private MongoTemplate mongoTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetTodosByUserEmail() throws Exception {
        TodoResponse response = new TodoResponse("1", "Buy milk", false, "Alice", "alice@test.com");
        when(todoService.getTodosByUserEmail("alice@test.com")).thenReturn(List.of(response));

        mockMvc.perform(get("/api/todos")
                .param("assigneeEmail", "alice@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"));
    }

    @Test
    void testGetTodoById() throws Exception {
        TodoResponse response = new TodoResponse("1", "Buy milk", false, "Alice", "alice@test.com");
        when(todoService.getTodoById("1")).thenReturn(response);

        mockMvc.perform(get("/api/todos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"));
    }

    @Test
    void testCreateTodo() throws Exception {
        CreateTodoRequest request = new CreateTodoRequest("Buy milk", "alice@test.com");
        TodoResponse response = new TodoResponse("1", "Buy milk", false, "Alice", "alice@test.com");
        
        when(todoService.createTodo(any())).thenReturn(response);

        mockMvc.perform(post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"));
    }

    @Test
    void testSetDone() throws Exception {
        TodoResponse response = new TodoResponse("1", "Buy milk", true, "Alice", "alice@test.com");
        when(todoService.setDone("1", true)).thenReturn(response);

        mockMvc.perform(patch("/api/todos/1/done")
                .contentType(MediaType.APPLICATION_JSON)
                .content("true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.done").value(true));
    }

    @Test
    void testAssign() throws Exception {
        AssignTodoRequest request = new AssignTodoRequest("bob@test.com");
        TodoResponse response = new TodoResponse("1", "Buy milk", false, "Bob", "bob@test.com");
        
        when(todoService.assign(eq("1"), any())).thenReturn(response);

        mockMvc.perform(patch("/api/todos/1/assignee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assigneeName").value("Bob"));
    }

    @Test
    void testEdit() throws Exception {
        EditTodoRequest request = new EditTodoRequest("Buy water");
        TodoResponse response = new TodoResponse("1", "Buy water", false, "Alice", "alice@test.com");
        
        when(todoService.edit(eq("1"), any())).thenReturn(response);

        mockMvc.perform(patch("/api/todos/1/description")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Buy water"));
    }

    @Test
    void testDeleteTodo() throws Exception {
        mockMvc.perform(delete("/api/todos/1"))
                .andExpect(status().isNoContent());
    }
}

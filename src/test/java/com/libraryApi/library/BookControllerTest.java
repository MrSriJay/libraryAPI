package com.libraryApi.library;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.libraryApi.library.controller.BookController;
import com.libraryApi.library.dto.BookRequest;
import com.libraryApi.library.dto.BookResponse;
import com.libraryApi.library.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    private BookRequest request;
    private BookResponse response;

    @BeforeEach
    void setUp() {
        request = new BookRequest();
        request.setIsbn("978-3-16-148410-0");
        request.setTitle("The Great Gatsby");
        request.setAuthor("F. Scott Fitzgerald");
        response = new BookResponse(1L, "978-3-16-148410-0", "The Great Gatsby", "F. Scott Fitzgerald", null);
    }

    @Test
    void registerBook_success() throws Exception {
        when(bookService.registerBook(any(BookRequest.class))).thenReturn(response);
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.isbn").value("978-3-16-148410-0"))
                .andExpect(jsonPath("$.title").value("The Great Gatsby"));
    }

    @Test
    void registerBook_invalidInput_returnsBadRequest() throws Exception {
        request.setIsbn(""); // Invalid ISBN
        when(bookService.registerBook(any(BookRequest.class)))
                .thenThrow(new jakarta.validation.ConstraintViolationException("ISBN required", null));
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllBooks_success() throws Exception {
        when(bookService.getAllBooks()).thenReturn(List.of(response));
        mockMvc.perform(get("/api/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("The Great Gatsby"));
    }
}
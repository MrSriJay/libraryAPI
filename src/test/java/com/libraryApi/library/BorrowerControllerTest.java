package com.libraryApi.library;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.libraryApi.library.controller.BorrowerController;
import com.libraryApi.library.dto.BorrowerRequest;
import com.libraryApi.library.dto.BorrowerResponse;
import com.libraryApi.library.service.BookService;
import com.libraryApi.library.service.BorrowerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BorrowerController.class)
class BorrowerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BorrowerService borrowerService;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    private BorrowerRequest request;
    private BorrowerResponse response;

    @BeforeEach
    void setUp() {
        request = new BorrowerRequest();
        request.setName("Pasindu Liyanage");
        request.setEmail("pasindu_l@gmail.com");
        response = new BorrowerResponse(1L, "Pasindu Liyanage", "pasindu_l@gmail.com");
    }

    @Test
    void registerBorrower_success() throws Exception {
        when(borrowerService.registerBorrower(any(BorrowerRequest.class))).thenReturn(response);
        mockMvc.perform(post("/api/borrowers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Pasindu Liyanage"))
                .andExpect(jsonPath("$.email").value("pasindu_l@gmail.com"));
    }

    @Test
    void registerBorrower_invalidInput_returnsBadRequest() throws Exception {
        request.setEmail("invalid"); // Invalid email
        when(borrowerService.registerBorrower(any(BorrowerRequest.class)))
                .thenThrow(new jakarta.validation.ConstraintViolationException("Invalid email", null));
        mockMvc.perform(post("/api/borrowers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void borrowBook_success() throws Exception {
        doNothing().when(bookService).borrowBook(1L, 1L);
        mockMvc.perform(post("/api/borrowers/1/borrow/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void returnBook_success() throws Exception {
        doNothing().when(bookService).returnBook(1L, 1L);
        mockMvc.perform(post("/api/borrowers/1/return/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
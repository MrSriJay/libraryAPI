package com.libraryApi.library.controller;

import com.libraryApi.library.dto.BookRequest;
import com.libraryApi.library.dto.BookResponse;
import com.libraryApi.library.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;


@Tag(name = "Book", description = "Books Management APIs")
@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @Operation(summary = "Register a new book", description = "Adds a new book with ISBN, title, and author")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Book created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or ISBN mismatch")
    })
    @PostMapping
    public ResponseEntity<BookResponse> register(@Valid @RequestBody BookRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.registerBook(request));
    }


    @Operation(summary = "List all books", description = "Retrieves all books with their details")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of books retrieved")
    })
    @GetMapping
    public ResponseEntity<List<BookResponse>> getAll() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }
}
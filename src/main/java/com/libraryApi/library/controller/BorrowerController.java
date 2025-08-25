package com.libraryApi.library.controller;

import com.libraryApi.library.dto.BorrowerRequest;
import com.libraryApi.library.dto.BorrowerResponse;
import com.libraryApi.library.service.BookService;
import com.libraryApi.library.service.BorrowerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Borrower", description = "Browser's Management APIs")
@RestController
@RequestMapping("/api/borrowers")
public class BorrowerController {
    private final BorrowerService borrowerService;
    private final BookService bookService;


    public BorrowerController(BorrowerService borrowerService, BookService bookService) {
        this.borrowerService = borrowerService;
        this.bookService = bookService;
    }

    @Operation(summary = "Register a new borrower", description = "Creates a new borrower with name and email")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Borrower created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or email already exists")
    })
    @PostMapping
    public ResponseEntity<BorrowerResponse> register(@Valid @RequestBody BorrowerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(borrowerService.registerBorrower(request));
    }

    @Operation(summary = "Borrow a book", description = "Allows a borrower to borrow an available book by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book borrowed successfully"),
            @ApiResponse(responseCode = "400", description = "Book already borrowed or invalid IDs"),
            @ApiResponse(responseCode = "404", description = "Borrower or book not found")
    })
    @PostMapping("/{borrowerId}/borrow/{bookId}")
    public ResponseEntity<Void> borrow(@PathVariable Long borrowerId, @PathVariable Long bookId) {
        bookService.borrowBook(borrowerId, bookId);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "Return a book", description = "Allows a borrower to return a borrowed book")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book returned successfully"),
            @ApiResponse(responseCode = "400", description = "Book not borrowed by this borrower"),
            @ApiResponse(responseCode = "404", description = "Borrower or book not found")
    })
    @PostMapping("/{borrowerId}/return/{bookId}")
    public ResponseEntity<Void> returnBook(@PathVariable Long borrowerId, @PathVariable Long bookId) {
        bookService.returnBook(borrowerId, bookId);
        return ResponseEntity.ok().build();
    }
}

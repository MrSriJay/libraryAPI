package com.libraryApi.library.dto;

import lombok.Data;

@Data
public class BookResponse {
    private Long id;
    private String isbn;
    private String title;
    private String author;
    private Long borrowedById;

    public BookResponse(Long id, String isbn, String title, String author, Long borrowedById) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.borrowedById = borrowedById;
    }
}
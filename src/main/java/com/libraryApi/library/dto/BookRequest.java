package com.libraryApi.library.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BookRequest {
    @NotBlank
    private String isbn;
    @NotBlank
    private String title;
    @NotBlank
    private String author;
}
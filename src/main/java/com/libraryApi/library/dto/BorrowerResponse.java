package com.libraryApi.library.dto;

import lombok.Data;

@Data
public class BorrowerResponse {
    private Long id;
    private String name;
    private String email;

    public BorrowerResponse(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
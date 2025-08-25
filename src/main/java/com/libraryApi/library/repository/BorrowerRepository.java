package com.libraryApi.library.repository;

import com.libraryApi.library.entity.Borrower;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BorrowerRepository extends JpaRepository<Borrower, Long> {
    boolean existsByEmail(String email);
}
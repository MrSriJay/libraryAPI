package com.libraryApi.library.repository;

import com.libraryApi.library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByIsbn(String isbn);

    @Query("SELECT b from Book b WHERE b.isbn = ?1 AND (b.title != ?2 OR b.author != ?3)")
    List<Book> findInconsistentByIsbn(String isbn, String title, String author);
}
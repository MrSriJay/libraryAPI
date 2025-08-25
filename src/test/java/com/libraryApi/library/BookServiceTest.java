package com.libraryApi.library;

import com.libraryApi.library.dto.BookRequest;
import com.libraryApi.library.dto.BookResponse;
import com.libraryApi.library.entity.Book;
import com.libraryApi.library.entity.Borrower;
import com.libraryApi.library.exception.BookAlreadyBorrowedException;
import com.libraryApi.library.exception.InvalidOperationException;
import com.libraryApi.library.exception.ResourceNotFoundException;
import com.libraryApi.library.repository.BookRepository;
import com.libraryApi.library.service.BookService;
import com.libraryApi.library.service.BorrowerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BorrowerService borrowerService;

    @InjectMocks
    private BookService bookService;

    private BookRequest bookRequest;
    private Borrower borrower;
    private Book book;

    @BeforeEach
    void setUp() {
        bookRequest = new BookRequest();
        bookRequest.setIsbn("978-3-16-148410-0");
        bookRequest.setTitle("The Great Gatsby");
        bookRequest.setAuthor("F. Scott Fitzgerald");

        borrower = new Borrower();
        borrower.setId(1L);
        borrower.setName("John Doe");
        borrower.setEmail("john.doe@example.com");

        book = new Book();
        book.setId(1L);
        book.setIsbn("978-3-16-148410-0");
        book.setTitle("The Great Gatsby");
        book.setAuthor("F. Scott Fitzgerald");
    }

    @Test
    void registerBook_success_newIsbn() {
        when(bookRepository.findByIsbn("978-3-16-148410-0")).thenReturn(Collections.emptyList());
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        BookResponse response = bookService.registerBook(bookRequest);
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("978-3-16-148410-0", response.getIsbn());
        assertEquals("The Great Gatsby", response.getTitle());
        assertNull(response.getBorrowedById());
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void registerBook_success_existingIsbn_sameDetails() {
        when(bookRepository.findByIsbn("978-3-16-148410-0")).thenReturn(List.of(book));
        Book savedBook = new Book();
        savedBook.setId(2L);
        savedBook.setIsbn("978-3-16-148410-0");
        savedBook.setTitle("The Great Gatsby");
        savedBook.setAuthor("F. Scott Fitzgerald");
        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);
        BookResponse response = bookService.registerBook(bookRequest);
        assertEquals(2L, response.getId());
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void registerBook_inconsistentIsbn_throwsException() {
        Book existing = new Book();
        existing.setIsbn("978-3-16-148410-0");
        existing.setTitle("Different Title");
        existing.setAuthor("Different Author");
        when(bookRepository.findByIsbn("978-3-16-148410-0")).thenReturn(List.of(existing));
        assertThrows(InvalidOperationException.class, () -> bookService.registerBook(bookRequest));
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void getAllBooks_success() {
        when(bookRepository.findAll()).thenReturn(List.of(book));
        List<BookResponse> books = bookService.getAllBooks();
        assertEquals(1, books.size());
        assertEquals("The Great Gatsby", books.get(0).getTitle());
        assertNull(books.get(0).getBorrowedById());
    }

    @Test
    void borrowBook_success() {
        when(borrowerService.findById(1L)).thenReturn(borrower);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        bookService.borrowBook(1L, 1L);
        verify(bookRepository).save(any(Book.class));
        assertEquals(borrower, book.getBorrowedBy());
    }

    @Test
    void borrowBook_bookNotFound_throwsException() {
        when(borrowerService.findById(1L)).thenReturn(borrower);
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> bookService.borrowBook(1L, 1L));
    }

    @Test
    void borrowBook_alreadyBorrowed_throwsException() {
        book.setBorrowedBy(borrower);
        when(borrowerService.findById(1L)).thenReturn(borrower);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        assertThrows(BookAlreadyBorrowedException.class, () -> bookService.borrowBook(1L, 1L));
    }

    @Test
    void returnBook_success() {
        book.setBorrowedBy(borrower);
        when(borrowerService.findById(1L)).thenReturn(borrower);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        bookService.returnBook(1L, 1L);
        verify(bookRepository).save(any(Book.class));
        assertNull(book.getBorrowedBy());
    }

    @Test
    void returnBook_notBorrowed_throwsException() {
        when(borrowerService.findById(1L)).thenReturn(borrower);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        assertThrows(InvalidOperationException.class, () -> bookService.returnBook(1L, 1L));
    }

    @Test
    void returnBook_wrongBorrower_throwsException() {
        Borrower otherBorrower = new Borrower();
        otherBorrower.setId(2L);
        book.setBorrowedBy(otherBorrower);
        when(borrowerService.findById(1L)).thenReturn(borrower);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        assertThrows(InvalidOperationException.class, () -> bookService.returnBook(1L, 1L));
    }
}
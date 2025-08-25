package com.libraryApi.library.service;

import com.libraryApi.library.dto.BookRequest;
import com.libraryApi.library.dto.BookResponse;
import com.libraryApi.library.entity.Book;
import com.libraryApi.library.entity.Borrower;
import com.libraryApi.library.exception.BookAlreadyBorrowedException;
import com.libraryApi.library.exception.InvalidOperationException;
import com.libraryApi.library.exception.ResourceNotFoundException;
import com.libraryApi.library.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final BorrowerService borrowerService;

    public BookService(BookRepository bookRepository, BorrowerService borrowerService) {
        this.bookRepository = bookRepository;
        this.borrowerService = borrowerService;
    }

    @Transactional
    public BookResponse registerBook(BookRequest request) {
        // Check ISBN consistency
        List<Book> existing = bookRepository.findByIsbn(request.getIsbn());
        if (!existing.isEmpty()) {
            Book first = existing.get(0);
            if (!first.getTitle().equals(request.getTitle()) || !first.getAuthor().equals(request.getAuthor())) {
                throw new InvalidOperationException("Title or author must match for same ISBN");
            }
        }
        Book book = new Book();
        book.setIsbn(request.getIsbn());
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        Book saved = bookRepository.save(book);
        return new BookResponse(saved.getId(), saved.getIsbn(), saved.getTitle(), saved.getAuthor(), null);
    }

    public List<BookResponse> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(b -> new BookResponse(b.getId(), b.getIsbn(), b.getTitle(), b.getAuthor(),
                        b.getBorrowedBy() != null ? b.getBorrowedBy().getId() : null))
                .collect(Collectors.toList());
    }

    @Transactional
    public void borrowBook(Long borrowerId, Long bookId) {
        Borrower borrower = borrowerService.findById(borrowerId);
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        if (book.getBorrowedBy() != null) {
            throw new BookAlreadyBorrowedException("Book is already borrowed");
        }
        book.setBorrowedBy(borrower);
        bookRepository.save(book);
    }

    @Transactional
    public void returnBook(Long borrowerId, Long bookId) {
        Borrower borrower = borrowerService.findById(borrowerId);
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        if (book.getBorrowedBy() == null || !book.getBorrowedBy().getId().equals(borrowerId)) {
            throw new InvalidOperationException("This book is not borrowed by you");
        }
        book.setBorrowedBy(null);
        bookRepository.save(book);
    }
}
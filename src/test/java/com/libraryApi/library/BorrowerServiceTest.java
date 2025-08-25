package com.libraryApi.library;

import com.libraryApi.library.dto.BorrowerRequest;
import com.libraryApi.library.dto.BorrowerResponse;
import com.libraryApi.library.entity.Borrower;
import com.libraryApi.library.exception.ResourceAlreadyExistsException;
import com.libraryApi.library.repository.BorrowerRepository;
import com.libraryApi.library.service.BorrowerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BorrowerServiceTest {

    @Mock
    private BorrowerRepository borrowerRepository;

    @InjectMocks
    private BorrowerService borrowerService;

    private BorrowerRequest request;

    @BeforeEach
    void setUp() {
        request = new BorrowerRequest();
        request.setName("Pasindu Liyanage");
        request.setEmail("pasindu_l@gmail.com");
    }

    @Test
    void registerBorrower_success() {
        when(borrowerRepository.existsByEmail("pasindu_l@gmail.com")).thenReturn(false);
        Borrower savedBorrower = new Borrower();
        savedBorrower.setId(1L);
        savedBorrower.setName("Pasindu Liyanage");
        savedBorrower.setEmail("pasindu_l@gmail.com");
        when(borrowerRepository.save(any(Borrower.class))).thenReturn(savedBorrower);
        BorrowerResponse response = borrowerService.registerBorrower(request);
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Pasindu Liyanage", response.getName());
        assertEquals("pasindu_l@gmail.com", response.getEmail());
        verify(borrowerRepository).save(any(Borrower.class));
    }

    @Test
    void registerBorrower_duplicateEmail_throwsException() {
        when(borrowerRepository.existsByEmail("pasindu_l@gmail.com")).thenReturn(true);
        assertThrows(ResourceAlreadyExistsException.class, () -> borrowerService.registerBorrower(request));
        verify(borrowerRepository, never()).save(any(Borrower.class));
    }

    @Test
    void findById_success() {
        Borrower borrower = new Borrower();
        borrower.setId(1L);
        borrower.setName("Pasindu Liyanage");
        borrower.setEmail("pasindu_l@gmail.com");
        when(borrowerRepository.findById(1L)).thenReturn(Optional.of(borrower));
        Borrower result = borrowerService.findById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Pasindu Liyanage", result.getName());
    }

    @Test
    void findById_notFound_throwsException() {
        when(borrowerRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> borrowerService.findById(1L));
    }
}
package com.libraryApi.library.service;

import com.libraryApi.library.dto.BorrowerRequest;
import com.libraryApi.library.dto.BorrowerResponse;
import com.libraryApi.library.entity.Borrower;
import com.libraryApi.library.exception.ResourceAlreadyExistsException;
import com.libraryApi.library.repository.BorrowerRepository;
import org.springframework.stereotype.Service;

@Service
public class BorrowerService {
    private final BorrowerRepository borrowerRepository;

    public BorrowerService(BorrowerRepository borrowerRepository) {
        this.borrowerRepository = borrowerRepository;
    }

    public BorrowerResponse registerBorrower(BorrowerRequest request) {
        if (borrowerRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Email is  already registered");
        }
        Borrower borrower = new Borrower();
        borrower.setName(request.getName());
        borrower.setEmail(request.getEmail());
        Borrower saved = borrowerRepository.save(borrower);
        return new BorrowerResponse(saved.getId(), saved.getName(), saved.getEmail());
    }

    public Borrower findById(Long id) {
        return borrowerRepository.findById(id).orElseThrow(() -> new RuntimeException("Borrower not found"));
    }
}
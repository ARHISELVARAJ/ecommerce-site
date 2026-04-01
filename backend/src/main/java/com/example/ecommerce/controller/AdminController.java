package com.example.ecommerce.controller;

import com.example.ecommerce.model.Seller;
import com.example.ecommerce.repository.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private SellerRepository sellerRepository;

    @GetMapping("/sellers/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Seller> getPendingSellers() {
        return sellerRepository.findAll().stream()
                .filter(s -> !s.isVerified())
                .toList();
    }

    @PostMapping("/verify-seller/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> verifySeller(@PathVariable String id) {
        Optional<Seller> seller = sellerRepository.findById(id);
        if (seller.isPresent()) {
            seller.get().setVerified(true);
            sellerRepository.save(seller.get());
            return ResponseEntity.ok("Seller verified successfully");
        }
        return ResponseEntity.notFound().build();
    }
}

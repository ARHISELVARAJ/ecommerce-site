package com.example.ecommerce.controller;

import com.example.ecommerce.model.Buyer;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.BuyerRepository;
import com.example.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/wishlist")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class WishlistController {

    @Autowired
    private BuyerRepository buyerRepository;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/{userId}")
    public ResponseEntity<List<Product>> getWishlist(@PathVariable String userId) {
        Buyer buyer = buyerRepository.findById(userId).orElse(null);
        if (buyer == null || buyer.getWishlist() == null || buyer.getWishlist().isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        List<Product> products = productRepository.findAllById(buyer.getWishlist());
        return ResponseEntity.ok(products);
    }

    @PostMapping("/{userId}/toggle/{productId}")
    public ResponseEntity<Set<String>> toggleWishlist(@PathVariable String userId, @PathVariable String productId) {
        Buyer buyer = buyerRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Set<String> wishlist = buyer.getWishlist();
        if (wishlist == null) {
            wishlist = new java.util.HashSet<>();
            buyer.setWishlist(wishlist);
        }

        if (wishlist.contains(productId)) {
            wishlist.remove(productId);
        } else {
            wishlist.add(productId);
        }
        
        buyerRepository.save(buyer);
        return ResponseEntity.ok(wishlist);
    }
}

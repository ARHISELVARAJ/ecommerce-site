package com.example.ecommerce.service;

import com.example.ecommerce.model.Buyer;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.repository.BuyerRepository;
import com.example.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {
    @Autowired
    private BuyerRepository buyerRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<CartItem> getCartByUserId(String userId) {
        return buyerRepository.findById(userId)
                .map(Buyer::getCart)
                .orElse(new ArrayList<>());
    }

    public List<CartItem> addToCart(String userId, String productId, int quantity) {
        Buyer buyer = buyerRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));
        
        if (buyer.getCart() == null) {
            buyer.setCart(new ArrayList<>());
        }

        Optional<CartItem> existingItem = buyer.getCart().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
        } else {
            buyer.getCart().add(new CartItem(productId, quantity));
        }
        buyerRepository.save(buyer);
        return buyer.getCart();
    }

    public List<CartItem> removeFromCart(String userId, String productId) {
        Buyer buyer = buyerRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));
        
        if (buyer.getCart() != null) {
            buyer.getCart().removeIf(item -> item.getProductId().equals(productId));
            buyerRepository.save(buyer);
        }
        return buyer.getCart();
    }
}

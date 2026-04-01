package com.example.ecommerce.controller;

import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @GetMapping("/{userId}")
    public List<CartItem> getCart(@PathVariable String userId) {
        return cartService.getCartByUserId(userId);
    }

    @PostMapping("/{userId}/add")
    public List<CartItem> addToCart(@PathVariable String userId, @RequestBody CartItem item) {
        return cartService.addToCart(userId, item.getProductId(), item.getQuantity());
    }

    @DeleteMapping("/{userId}/remove/{productId}")
    public List<CartItem> removeFromCart(@PathVariable String userId, @PathVariable String productId) {
        return cartService.removeFromCart(userId, productId);
    }
}

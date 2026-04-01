package com.example.ecommerce.controller;

import com.example.ecommerce.model.Buyer;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.service.AuthService;
import com.example.ecommerce.repository.BuyerRepository;
import com.example.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BuyerRepository buyerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private JavaMailSender mailSender;

    @PostMapping("/purchase")
    public ResponseEntity<?> purchase(@RequestBody Order order, @RequestParam(required = false) boolean usePoints) {
        // Validate and update stock, set sellerId and status for items
        for (com.example.ecommerce.model.Order.OrderItem item : order.getItems()) {
            Optional<com.example.ecommerce.model.Product> productOpt = productRepository.findById(item.getProductId());
            if (productOpt.isEmpty() || productOpt.get().getStock() < item.getQuantity()) {
                return ResponseEntity.badRequest().body("Insufficient stock for product: " + 
                    (productOpt.isPresent() ? productOpt.get().getName() : item.getProductId()));
            }
            // Populate Seller Info & Initial Status
            item.setSellerId(productOpt.get().getSellerId());
            item.setProductName(productOpt.get().getName());
            item.setProductImage(productOpt.get().getImageUrl());
            item.setStatus("PROCESSING");
        }

        // Decrement stock
        for (com.example.ecommerce.model.Order.OrderItem item : order.getItems()) {
            com.example.ecommerce.model.Product product = productRepository.findById(item.getProductId()).get();
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
        }

        order.setStatus("PROCESSING");
        order.setCreatedAt(new java.util.Date());
        
        // Handle Points & Cart Clearing
        Optional<Buyer> buyerOpt = buyerRepository.findById(order.getBuyerId());
        if (buyerOpt.isPresent()) {
            Buyer buyer = buyerOpt.get();
            
            if (usePoints) {
                double discount = Math.min(buyer.getPoints(), order.getTotalAmount());
                order.setDiscountApplied(discount);
                order.setTotalAmount(Math.max(0, order.getTotalAmount() - discount));
                
                int earnedPoints = (int) (order.getTotalAmount() / 100);
                buyer.setPoints((int)(buyer.getPoints() - discount) + earnedPoints);
            } else {
                int earnedPoints = (int) (order.getTotalAmount() / 100);
                buyer.setPoints(buyer.getPoints() + earnedPoints);
            }

            buyer.getCart().clear();
            buyerRepository.save(buyer);
        }

        Order savedOrder = orderRepository.save(order);
        
        if (buyerOpt.isPresent()) {
            Buyer buyer = buyerOpt.get();
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom("arhi.24.2007@gmail.com");
                message.setTo(buyer.getEmail());
                message.setSubject("Cartify Order Confirmation - #" + savedOrder.getId().substring(0,8).toUpperCase());
                message.setText("Thank you for your order, " + buyer.getUsername() + "!\n\n" +
                                "Total Amount: ₹" + savedOrder.getTotalAmount() + "\n" +
                                "Discount Applied: ₹" + savedOrder.getDiscountApplied() + "\n" +
                                "Shipping to: " + savedOrder.getShippingAddress() + "\n\n" +
                                "We are processing your items for shipping.\n\n" +
                                "Best regards,\nCartify Team");
                mailSender.send(message);
            } catch (Exception e) {
                System.out.println("Email failed but order saved: " + e.getMessage());
            }
        }
        
        return ResponseEntity.ok(savedOrder);
    }

    @GetMapping("/orders/{userId}")
    public List<Order> getOrders(@PathVariable String userId) {
        return orderRepository.findByBuyerId(userId);
    }

    @GetMapping("/seller/orders/{sellerId}")
    public List<Order> getSellerOrders(@PathVariable String sellerId) {
        // Find all orders where at least one item belongs to this seller
        return orderRepository.findAll().stream()
            .filter(o -> o.getItems().stream().anyMatch(i -> sellerId.equals(i.getSellerId())))
            .toList();
    }

    @PutMapping("/order/{orderId}/item/{productId}/status")
    public ResponseEntity<?> updateItemStatus(@PathVariable String orderId, @PathVariable String productId, @RequestParam String status) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.getItems().stream()
                .filter(i -> i.getProductId().equals(productId))
                .forEach(i -> i.setStatus(status));
            
            // If all items are delivered, mark order as COMPLETED
            boolean allDelivered = order.getItems().stream().allMatch(i -> "DELIVERED".equals(i.getStatus()));
            if (allDelivered) order.setStatus("COMPLETED");
            
            orderRepository.save(order);
            return ResponseEntity.ok("Status updated to " + status);
        }
        return ResponseEntity.notFound().build();
    }
}

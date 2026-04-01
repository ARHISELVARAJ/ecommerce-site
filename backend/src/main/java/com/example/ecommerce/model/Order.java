package com.example.ecommerce.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "orders")
public class Order {
    @Id
    private String id;
    private String buyerId;
    private String shippingAddress;
    private List<OrderItem> items;
    private double totalAmount;
    private String status; // Overall status: PROCESSING, COMPLETED
    private double discountApplied = 0.0;
    private java.util.Date createdAt = new java.util.Date();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItem {
        private String productId;
        private String productName;
        private String productImage;
        private int quantity;
        private double price;
        private String sellerId;
        private String status; // PROCESSING, SHIPPED, DELIVERED
    }
}

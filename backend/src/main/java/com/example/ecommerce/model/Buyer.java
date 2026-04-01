package com.example.ecommerce.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "buyers")
public class Buyer {
    @Id
    private String id;
    private String username;
    private String password;
    private String email;
    private int points = 0;
    private List<CartItem> cart = new java.util.ArrayList<>();
    private java.util.Set<String> wishlist = new java.util.HashSet<>();
    private Set<String> roles; // ROLE_BUYER
}

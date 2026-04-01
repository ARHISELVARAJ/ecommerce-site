package com.example.ecommerce.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "sellers")
public class Seller {
    @Id
    private String id;
    private String username;
    private String password;
    private String email;
    private String businessName;
    private String businessAddress;
    private String phoneNumber;
    private boolean isVerified = false;
    private Set<String> roles; // ROLE_SELLER
}

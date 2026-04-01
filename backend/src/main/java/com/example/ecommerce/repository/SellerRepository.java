package com.example.ecommerce.repository;

import com.example.ecommerce.model.Seller;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface SellerRepository extends MongoRepository<Seller, String> {
    Optional<Seller> findByUsername(String username);
    Optional<Seller> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}

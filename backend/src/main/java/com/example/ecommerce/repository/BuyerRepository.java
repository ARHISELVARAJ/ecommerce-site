package com.example.ecommerce.repository;

import com.example.ecommerce.model.Buyer;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface BuyerRepository extends MongoRepository<Buyer, String> {
    Optional<Buyer> findByUsername(String username);
    Optional<Buyer> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}

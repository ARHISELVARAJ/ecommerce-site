package com.example.ecommerce.repository;

import com.example.ecommerce.model.OTP;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface OTPRepository extends MongoRepository<OTP, String> {
    Optional<OTP> findByEmailAndCode(String email, String code);
    void deleteByEmail(String email);
}

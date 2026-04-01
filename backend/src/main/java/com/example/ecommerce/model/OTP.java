package com.example.ecommerce.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "otps")
public class OTP {
    @Id
    private String id;
    private String email;
    private String code;
    private LocalDateTime expiryTime;

    public OTP() {}

    public OTP(String email, String code, int expiryMinutes) {
        this.email = email;
        this.code = code;
        this.expiryTime = LocalDateTime.now().plusMinutes(expiryMinutes);
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public LocalDateTime getExpiryTime() { return expiryTime; }
    public void setExpiryTime(LocalDateTime expiryTime) { this.expiryTime = expiryTime; }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryTime);
    }
}

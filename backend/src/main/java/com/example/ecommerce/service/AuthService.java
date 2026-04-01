package com.example.ecommerce.service;

import com.example.ecommerce.model.Buyer;
import com.example.ecommerce.model.Seller;
import com.example.ecommerce.model.OTP;
import com.example.ecommerce.repository.BuyerRepository;
import com.example.ecommerce.repository.SellerRepository;
import com.example.ecommerce.repository.OTPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Random;
import java.util.Optional;
import java.util.Collections;

@Service
public class AuthService {
    @Autowired
    private BuyerRepository buyerRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private OTPRepository otpRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    public void sendOTP(String email) {
        String cleanEmail = email.toLowerCase();
        String code = String.format("%06d", new Random().nextInt(999999));
        otpRepository.deleteByEmail(cleanEmail);
        otpRepository.save(new OTP(cleanEmail, code, 5));
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("arhi.24.2007@gmail.com");
        message.setTo(cleanEmail);
        message.setSubject("Your Cartify Verification Code");
        message.setText("Your OTP for Cartify is: " + code + "\nValid for 5 minutes.");
        mailSender.send(message);
        
        System.out.println("DEBUG: Real email sent to " + cleanEmail);
    }

    public boolean verifyOTP(String email, String code) {
        String cleanEmail = email.toLowerCase();
        Optional<OTP> otp = otpRepository.findByEmailAndCode(cleanEmail, code);
        if (otp.isPresent() && !otp.get().isExpired()) {
            otpRepository.deleteByEmail(cleanEmail);
            return true;
        }
        return false;
    }

    public Optional<Buyer> getBuyerByUsername(String username) {
        return buyerRepository.findByUsername(username);
    }

    public Optional<Seller> getSellerByUsername(String username) {
        return sellerRepository.findByUsername(username);
    }

    public String getEmailByUsername(String username) {
        Optional<Buyer> buyer = buyerRepository.findAll().stream()
                .filter(b -> b.getUsername().equals(username))
                .findFirst();
        if (buyer.isPresent()) return buyer.get().getEmail();

        Optional<Seller> seller = sellerRepository.findAll().stream()
                .filter(s -> s.getUsername().equals(username))
                .findFirst();
        if (seller.isPresent()) return seller.get().getEmail();

        return null;
    }

    public Buyer registerBuyer(Buyer buyer) {
        String cleanEmail = buyer.getEmail().toLowerCase();
        if (buyerRepository.findByUsername(buyer.getUsername()).isPresent() || 
            sellerRepository.findByUsername(buyer.getUsername()).isPresent()) {
            throw new RuntimeException("Username is already taken globally");
        }
        if (buyerRepository.findByEmail(cleanEmail).isPresent() || 
            sellerRepository.findByEmail(cleanEmail).isPresent()) {
            throw new RuntimeException("Email is already in use globally");
        }
        buyer.setEmail(cleanEmail);
        buyer.setPassword(passwordEncoder.encode(buyer.getPassword()));
        buyer.setRoles(Collections.singleton("ROLE_BUYER"));
        return buyerRepository.save(buyer);
    }

    public Seller registerSeller(Seller seller) {
        String cleanEmail = seller.getEmail().toLowerCase();
        if (sellerRepository.findByUsername(seller.getUsername()).isPresent() || 
            buyerRepository.findByUsername(seller.getUsername()).isPresent()) {
            throw new RuntimeException("Username is already taken globally");
        }
        if (sellerRepository.findByEmail(cleanEmail).isPresent() || 
            buyerRepository.findByEmail(cleanEmail).isPresent()) {
            throw new RuntimeException("Email is already in use globally");
        }
        seller.setEmail(cleanEmail);
        seller.setPassword(passwordEncoder.encode(seller.getPassword()));
        seller.setRoles(Collections.singleton("ROLE_SELLER"));
        seller.setVerified(true); // Auto-verify for easy testing
        return sellerRepository.save(seller);
    }
}

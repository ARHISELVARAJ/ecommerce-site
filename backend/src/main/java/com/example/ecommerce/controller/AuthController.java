package com.example.ecommerce.controller;

import com.example.ecommerce.model.Buyer;
import com.example.ecommerce.model.Seller;
import com.example.ecommerce.security.JwtUtils;
import com.example.ecommerce.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/register/buyer")
    public ResponseEntity<?> registerBuyer(@RequestBody Buyer buyer) {
        return ResponseEntity.ok(authService.registerBuyer(buyer));
    }

    @PostMapping("/register/seller")
    public ResponseEntity<?> registerSeller(@RequestBody Seller seller) {
        return ResponseEntity.ok(authService.registerSeller(seller));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        org.springframework.security.core.userdetails.User userDetails = 
                (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        
        String userId = "";
        String email = "";
        int points = 0;
        
        Map<String, Object> response = new HashMap<>();

        Optional<Buyer> buyer = authService.getBuyerByUsername(userDetails.getUsername());
        if (buyer.isPresent()) {
            userId = buyer.get().getId();
            email = buyer.get().getEmail();
            points = buyer.get().getPoints();
            response.put("cart", buyer.get().getCart());
        } else {
            Optional<Seller> seller = authService.getSellerByUsername(userDetails.getUsername());
            if (seller.isPresent()) {
                userId = seller.get().getId();
                email = seller.get().getEmail();
            }
        }

        response.put("token", jwt);
        response.put("id", userId);
        response.put("username", userDetails.getUsername());
        response.put("email", email);
        response.put("points", points);
        response.put("roles", userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList()));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOTP(@RequestParam String email) {
        authService.sendOTP(email.toLowerCase());
        return ResponseEntity.ok("OTP sent successfully");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOTP(@RequestParam String email, @RequestParam String code) {
        if (authService.verifyOTP(email.toLowerCase(), code)) {
            return ResponseEntity.ok("OTP verified successfully");
        }
        return ResponseEntity.status(401).body("Invalid or expired OTP");
    }

    @GetMapping("/email/{username}")
    public ResponseEntity<?> getEmailByUsername(@PathVariable String username) {
        String email = authService.getEmailByUsername(username);
        if (email != null) {
            Map<String, String> response = new HashMap<>();
            response.put("email", email);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/profile/{username}")
    public ResponseEntity<?> getUserProfile(@PathVariable String username) {
        // Try Seller FIRST
        Optional<Seller> seller = authService.getSellerByUsername(username);
        if (seller.isPresent()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", seller.get().getId());
            map.put("username", seller.get().getUsername());
            map.put("email", seller.get().getEmail());
            // Inject ROLE_SELLER if empty
            map.put("roles", (seller.get().getRoles() == null || seller.get().getRoles().isEmpty()) 
                ? java.util.Collections.singleton("ROLE_SELLER") : seller.get().getRoles());
            return ResponseEntity.ok(map);
        }

        // Fallback to Buyer
        Optional<Buyer> buyer = authService.getBuyerByUsername(username);
        if (buyer.isPresent()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", buyer.get().getId());
            map.put("username", buyer.get().getUsername());
            map.put("email", buyer.get().getEmail());
            map.put("points", buyer.get().getPoints());
            // Inject ROLE_BUYER if empty
            map.put("roles", (buyer.get().getRoles() == null || buyer.get().getRoles().isEmpty()) 
                ? java.util.Collections.singleton("ROLE_BUYER") : buyer.get().getRoles());
            map.put("cart", buyer.get().getCart());
            return ResponseEntity.ok(map);
        }
        return ResponseEntity.notFound().build();
    }
}

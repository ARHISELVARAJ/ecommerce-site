package com.example.ecommerce.security;

import com.example.ecommerce.model.Buyer;
import com.example.ecommerce.model.Seller;
import com.example.ecommerce.repository.BuyerRepository;
import com.example.ecommerce.repository.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private BuyerRepository buyerRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        String input = usernameOrEmail.toLowerCase();
        
        // Try Seller FIRST to prevent shadowing for those working on dashboards
        Optional<Seller> seller = sellerRepository.findByUsername(usernameOrEmail);
        if (!seller.isPresent()) {
            seller = sellerRepository.findByEmail(input);
        }
        if (seller.isPresent()) {
            return new org.springframework.security.core.userdetails.User(
                    seller.get().getUsername(),
                    seller.get().getPassword(),
                    seller.get().getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
            );
        }

        // Fallback to Buyer
        Optional<Buyer> buyer = buyerRepository.findByUsername(usernameOrEmail);
        if (!buyer.isPresent()) {
            buyer = buyerRepository.findByEmail(input);
        }
        if (buyer.isPresent()) {
            return new org.springframework.security.core.userdetails.User(
                    buyer.get().getUsername(),
                    buyer.get().getPassword(),
                    buyer.get().getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
            );
        }

        throw new UsernameNotFoundException("User Not Found with: " + usernameOrEmail);
    }
}

package com.example.ecommerce.service;

import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.Seller;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
        "a", "an", "the", "for", "and", "or", "in", "on", "at", "to", "with", "is", "of"
    ));

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> searchProducts(String text) {
        if (text == null || text.trim().isEmpty()) {
            return getAllProducts();
        }
        
        List<String> keywords = Arrays.stream(text.toLowerCase().split("\\s+"))
            .filter(word -> word.length() >= 2 && !STOP_WORDS.contains(word))
            .collect(Collectors.toList());

        if (keywords.isEmpty()) {
            return productRepository.findByNameContainingIgnoreCase(text);
        }

        List<Criteria> keywordCriterias = new java.util.ArrayList<>();
        for (String word : keywords) {
            keywordCriterias.add(new Criteria().orOperator(
                Criteria.where("name").regex(word, "i"),
                Criteria.where("description").regex(word, "i"),
                Criteria.where("category").regex(word, "i")
            ));
        }

        // Use andOperator so that ALL significant keywords must match at least one field
        Query query = new Query(new Criteria().andOperator(keywordCriterias.toArray(new Criteria[0])));
        return mongoTemplate.find(query, Product.class);
    }

    public Optional<Product> getProductById(String id) {
        return productRepository.findById(id);
    }

    public Product addProduct(Product product) {
        if (product.getSellerId() != null) {
            Optional<Seller> seller = sellerRepository.findById(product.getSellerId());
            if (seller.isPresent()) {
                return productRepository.save(product);
            } else {
                throw new RuntimeException("Seller not found");
            }
        }
        return productRepository.save(product); // Fallback for admin
    }

    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }
}

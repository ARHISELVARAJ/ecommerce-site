package com.example.ecommerce.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Redirect all non-API paths to index.html for React Router compatibility
        // This ensures refreshes on /seller, /cart, etc. work perfectly.
        registry.addViewController("/{path:[^\\.]*}")
                .setViewName("forward:/index.html");
                
        registry.addViewController("/{path:[^\\.]*}/{path:[^\\.]*}")
                .setViewName("forward:/index.html");
    }
}

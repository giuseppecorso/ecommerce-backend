package com.giuseppe.ecommerce.config;

import com.giuseppe.ecommerce.repository.UserRepository;
import com.giuseppe.ecommerce.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner createAdminUser(UserService userService, UserRepository userRepository) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                userService.registerUser("admin", "admin123", "ADMIN");
            }
        };
    }

    @Bean
    public CommandLineRunner createUser(UserService userService, UserRepository userRepository) {
        return args -> {
            if (userRepository.findByUsername("user").isEmpty()) {
                userService.registerUser("user", "user123", "USER");
            }
        };
    }
}

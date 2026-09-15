package com.giuseppe.ecommerce.config;

import com.giuseppe.ecommerce.model.User;
import com.giuseppe.ecommerce.repository.UserRepository;
import com.giuseppe.ecommerce.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

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
}

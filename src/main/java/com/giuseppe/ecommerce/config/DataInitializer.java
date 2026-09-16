package com.giuseppe.ecommerce.config;

import com.giuseppe.ecommerce.repository.UserRepository;
import com.giuseppe.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner createAdminUser(UserService userService,
                                             UserRepository userRepository,
                                             @Value("${app.admin.password}") String adminPassword) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                userService.registerUser("admin", adminPassword, "ADMIN");
            }
        };
    }

    @Bean
    public CommandLineRunner createUser(UserService userService, UserRepository userRepository, @Value("${app.user.password}") String userPassword) {
        return args -> {
            if (userRepository.findByUsername("user").isEmpty()) {
                userService.registerUser("user",userPassword, "USER");
            }
        };
    }
}

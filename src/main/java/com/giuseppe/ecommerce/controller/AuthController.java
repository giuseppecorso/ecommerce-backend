package com.giuseppe.ecommerce.controller;

import com.giuseppe.ecommerce.dto.RegisterRequest;
import com.giuseppe.ecommerce.dto.UserResponse;
import com.giuseppe.ecommerce.model.User;
import com.giuseppe.ecommerce.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@RestController
@Tag(name = "Auth Controller")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/api/auth/register")
    @Operation(summary = "Create a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "400", description = "Invalid user data"),
            @ApiResponse(responseCode = "409", description = "Username already taken")
    })
    public ResponseEntity<UserResponse> createUser(@RequestBody @Valid RegisterRequest registerRequest) {

        Optional<User> box = userService.registerUser(registerRequest.getUsername(), registerRequest.getPassword(), "USER");

        if (box.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } else {

            UserResponse resp = new UserResponse(box.get().getId(), box.get().getUsername(), box.get().getRole());

            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        }
    }
}

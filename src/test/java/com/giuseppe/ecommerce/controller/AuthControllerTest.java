package com.giuseppe.ecommerce.controller;

import com.giuseppe.ecommerce.config.SecurityConfig;
import com.giuseppe.ecommerce.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
public class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    UserService userService;
    @MockitoBean
    JwtEncoder jwtEncoder;
    @MockitoBean
    JwtDecoder jwtDecoder;

    @Test
    void registerReturns409WhenUsernameAlreadyExists() throws Exception {
        Mockito.when(userService.registerUser(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"mario\", \"password\": \"password123\"}"))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }
}
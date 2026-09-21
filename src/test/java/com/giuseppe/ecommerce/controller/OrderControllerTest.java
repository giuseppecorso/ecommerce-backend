package com.giuseppe.ecommerce.controller;

import com.giuseppe.ecommerce.config.SecurityConfig;
import com.giuseppe.ecommerce.mapper.OrderMapper;
import com.giuseppe.ecommerce.service.OrderService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(OrderController.class)
@Import(SecurityConfig.class)
public class OrderControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    OrderService orderService;
    @MockitoBean
    OrderMapper orderMapper;
    @MockitoBean
    JwtDecoder jwtDecoder;

    @Test
    void getOrdersReturns401WhenNotAuthenticated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/orders"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void patchStatusReturns403WhenUserIsNotAdmin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/orders/5/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\": \"PAID\"}"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());

        Mockito.verify(orderService, Mockito.never()).updateStatus(Mockito.any(), Mockito.any());
    }
}
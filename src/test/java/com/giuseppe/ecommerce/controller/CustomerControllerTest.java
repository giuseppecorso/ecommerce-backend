package com.giuseppe.ecommerce.controller;

import com.giuseppe.ecommerce.model.Customer;
import com.giuseppe.ecommerce.service.CustomerService;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    CustomerService customerService;

    @Test
    void postCustomerReturns201WhenDataIsValid () throws Exception {
        Customer cust = new Customer(1L, "Giuseppe", "Corso", "giuscorso@gmail.com");
        Mockito.when(customerService.createCustomer(Mockito.any())).thenReturn(cust);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\": \"Giuseppe\", \"lastName\": \"Corso\", \"email\": \"giuseppe@test.it\"}"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Giuseppe"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Corso"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("giuscorso@gmail.com"));
    }

    @Test
    void postCustomerReturns400WhenEmailIsInvalid  () throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\": \"Giuseppe\", \"lastName\": \"Corso\", \"email\": \"peppepuntocom\"}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").exists());

        Mockito.verify(customerService, Mockito.never()).createCustomer(Mockito.any());
    }

    @Test
    void getCustomerReturns404WhenCustomerDoesNotExist () throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/api/customers/999"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }
}

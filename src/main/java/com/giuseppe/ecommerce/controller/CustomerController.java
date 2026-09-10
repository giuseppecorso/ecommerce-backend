package com.giuseppe.ecommerce.controller;

import com.giuseppe.ecommerce.dto.CustomerRequest;
import com.giuseppe.ecommerce.dto.CustomerResponse;
import com.giuseppe.ecommerce.model.Customer;
import com.giuseppe.ecommerce.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@Tag(name = "Customer Controller")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/api/customers")
    @Operation(summary = "Create a new customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Customer created"),
            @ApiResponse(responseCode = "400", description = "Invalid customer data"),
    })
    public ResponseEntity<CustomerResponse> addCustomer(@RequestBody @Valid CustomerRequest req) {

        Customer customer = customerService.createCustomer(req);

        CustomerResponse resp = new CustomerResponse(customer.getId(),  customer.getFirstName(), customer.getLastName(), customer.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @GetMapping ("/api/customers/{id}")
    @Operation (summary = "Get customer by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operation successful"),
            @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content)
    })
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable Long id) {
        Optional<Customer> box = customerService.getCustomerById(id);

        if (box.isPresent()) {
            Customer cust = box.get();
            CustomerResponse found = new CustomerResponse(cust.getId(), cust.getFirstName(), cust.getLastName(), cust.getEmail());
            return ResponseEntity.ok(found);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

package com.giuseppe.ecommerce;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello World!";
    }

    @GetMapping("/status")
    public String status() {
        return "Start of new E-Commerce";
    }
}

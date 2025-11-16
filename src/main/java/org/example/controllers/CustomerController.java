package org.example.controllers;

import org.example.repository.CustomerRepository;
import org.example.repository.MerchantRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomerController {

    private final CustomerRepository customerRepository;

    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping("/gui/customer/profile")
    public String customerProfile(Model model, Authentication authentication) {
        String email = authentication.getName();
        var customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        model.addAttribute("customer", customer);
        return "customer-profile";
    }
}

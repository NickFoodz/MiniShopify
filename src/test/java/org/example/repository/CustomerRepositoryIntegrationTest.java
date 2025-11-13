package org.example.repository;

import org.example.ShopAppApplication;
import org.example.models.Customer;
import org.example.models.Merchant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for CustomerRepository
 * Tests customer CRUD operations and relationships
 */
@SpringBootTest(classes = ShopAppApplication.class)
public class CustomerRepositoryIntegrationTest {

    @Autowired
    private CustomerRepository customerRepository;

    /**
     * Tests the behavior of testSaveAndFindCustomer().
     */
    @Test
    void testSaveAndFindCustomer() {
        Customer customer = new Customer();
        customer.setName("John Doe");
        customer.setEmail("john.doe@example.com");
        customer.setPassword("password");

        Customer saved = customerRepository.save(customer);

        // Validate expected outcomes
        assertNotNull(saved.getId());
        // Validate expected outcomes
        assertEquals("John Doe", saved.getName());
        // Validate expected outcomes
        assertEquals("john.doe@example.com", saved.getEmail());

        Optional<Customer> found = customerRepository.findById(saved.getId());
        // Validate expected outcomes
        assertTrue(found.isPresent());
        // Validate expected outcomes
        assertEquals("john.doe@example.com", found.get().getEmail());

        // Validate expected outcome
        assertEquals("password", found.get().getPassword());
    }

    /**
     * Tests update customer
     */
    @Test
    void testUpdateCustomer() {
        Customer customer = new Customer();
        customer.setName("Bob Johnson");
        customer.setEmail("bob@example.com");
        customer.setPassword("password");

        Customer saved = customerRepository.save(customer);

        saved.setEmail("bob.johnson@newdomain.com");
        customerRepository.save(saved);

        Optional<Customer> updated = customerRepository.findById(saved.getId());
        // Validate expected outcomes
        assertTrue(updated.isPresent());
        // Validate expected outcomes
        assertEquals("bob.johnson@newdomain.com", updated.get().getEmail());
    }


    /**
     * Tests the behavior of testDeleteCustomer().
    */
    @Test
    void testDeleteMerchant() {
        Customer customer = new Customer();
        customer.setName("Alice Williams");
        customer.setEmail("alice@example.com");
        customer.setPassword("password");

        Customer saved = customerRepository.save(customer);
        Long merchantId = saved.getId();

        customerRepository.deleteById(merchantId);

        // Validate expected outcomes
        assertFalse(customerRepository.findById(merchantId).isPresent());
    }
}

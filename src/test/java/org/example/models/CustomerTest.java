package org.example.models;

import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomerTest {

    private Customer customer;

    @BeforeEach
    void setUp() {
       customer = new Customer();
       customer.setId(1L);
       customer.setName("TestName");
       customer.setEmail("email@email.com");
       customer.setPassword("password");

    }

    @AfterEach
    void tearDown() {
    }

    /**
     * Tests getId() in Merchant class
     */
    @Test
    void getId() {
        assertEquals(1L, customer.getId());
    }

    /**
     * Tests setId() in Merchant class
     */
    @Test
    void setId() {
        customer.setId(2L);
        assertEquals(2L, customer.getId());
    }

    /**
     * Tests getName() in Merchant class
     */
    @Test
    void getName() {
        assertEquals("TestName", customer.getName());
    }

    /**
     * Tests setName() in Merchant class
     */
    @Test
    void setName() {
        customer.setName("TestName");
        assertEquals("TestName", customer.getName());
    }

    /**
     * Tests getEmail() in Merchant class
     */
    @Test
    void getEmail() {
        assertEquals("email@email.com", customer.getEmail());
    }

    /**
     * Tests setEmail() in Merchant class
     */
    @Test
    void setEmail() {
        customer.setEmail("TestEmail2");
        assertEquals("TestEmail2", customer.getEmail());
    }

    /**
     * Tests setPassword() in Customer Class
     */
    @Test
    void setPassword() {
        customer.setPassword("password1");
        assertEquals("password1", customer.getPassword());
    }

    /**
     * Tests getPassword() in Customer Class
     */
    @Test
    void getPassword() {
        assertEquals("password", customer.getPassword());
    }

}

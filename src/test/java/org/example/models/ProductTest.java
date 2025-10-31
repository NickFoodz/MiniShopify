package org.example.models;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Product model
 * @author Nick Fuda
 * @version 1.0
 */
class ProductTest {

    Product testProduct;
    Shop testShop;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(1.0);
        testProduct.setStock(1);
        testShop = new Shop();
        testProduct.setShop(testShop);

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getId() {
        assertEquals(1L, testProduct.getId());
    }

    @Test
    void setId() {
        testProduct.setId(2L);
        assertEquals(2L, testProduct.getId());
    }

    @Test
    void getName() {
        assertEquals("Test Product", testProduct.getName());
    }

    @Test
    void setName() {
        testProduct.setName("Test Product setName");
        assertEquals("Test Product setName", testProduct.getName());
    }

    @Test
    void getDescription() {
        assertEquals("Test Description", testProduct.getDescription());
    }

    @Test
    void setDescription() {
        testProduct.setDescription("Test Product Halloween Spooky OoooOoOooo");
        assertEquals("Test Product Halloween Spooky OoooOoOooo", testProduct.getDescription());
    }

    @Test
    void getPrice() {
        assertEquals(1.0, testProduct.getPrice());
    }

    @Test
    void setPrice() {
        testProduct.setPrice(2.0);
        assertEquals(2.0, testProduct.getPrice());
    }

    @Test
    void getStock() {
        assertEquals(1, testProduct.getStock());
    }

    @Test
    void setStock() {
        testProduct.setStock(3);
        assertEquals(3, testProduct.getStock());
    }

    @Test
    void getShop() {
        assertEquals(testShop, testProduct.getShop());
    }

    @Test
    void setShop() {
        Shop testShop2 = new Shop();
        testProduct.setShop(testShop2);
        assertEquals(testShop2, testProduct.getShop());
    }
}
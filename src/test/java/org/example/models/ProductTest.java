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

    /**
     * Tests getId() in Product class
     */
    @Test
    void getId() {
        assertEquals(1L, testProduct.getId());
    }

    /**
     * Tests setId() in Product class
     */
    @Test
    void setId() {
        testProduct.setId(2L);
        assertEquals(2L, testProduct.getId());
    }

    /**
     * Tests getName() in Product class
     */
    @Test
    void getName() {
        assertEquals("Test Product", testProduct.getName());
    }

    /**
     * Tests setName() in Product class
     */
    @Test
    void setName() {
        testProduct.setName("Test Product setName");
        assertEquals("Test Product setName", testProduct.getName());
    }

    /**
     * Tests getDescription() in Product class
     */
    @Test
    void getDescription() {
        assertEquals("Test Description", testProduct.getDescription());
    }

    /**
     * Tests setDescription() in Product class
     */
    @Test
    void setDescription() {
        testProduct.setDescription("Test Product Halloween Spooky OoooOoOooo");
        assertEquals("Test Product Halloween Spooky OoooOoOooo", testProduct.getDescription());
    }

    /**
     * Tests getPrice() in Product class
     */
    @Test
    void getPrice() {
        assertEquals(1.0, testProduct.getPrice());
    }

    /**
     * Tests setPrice() in Product class
     */
    @Test
    void setPrice() {
        testProduct.setPrice(2.0);
        assertEquals(2.0, testProduct.getPrice());
    }

    /**
     * Tests getStock() in Product class
     */
    @Test
    void getStock() {
        assertEquals(1, testProduct.getStock());
    }

    /**
     * Tests setStock() in Product class
     */
    @Test
    void setStock() {
        testProduct.setStock(3);
        assertEquals(3, testProduct.getStock());
    }

    /**
     * Tests getShop() in Product class
     */
    @Test
    void getShop() {
        assertEquals(testShop, testProduct.getShop());
    }

    /**
     * Tests setShop() in Product class
     */
    @Test
    void setShop() {
        Shop testShop2 = new Shop();
        testProduct.setShop(testShop2);
        assertEquals(testShop2, testProduct.getShop());
    }

    /**
     * Tests addStock() to add to the stock of the item
     */
    @Test
    void addStock() {
        Product testaddProduct = new Product();
        testaddProduct.setId(6L);
        testaddProduct.setName("Test Product");
        testaddProduct.setDescription("Test Description");
        testaddProduct.setPrice(1.0);
        testaddProduct.setStock(8);
        assertEquals(8, testaddProduct.getStock());
        testaddProduct.addStock(1);
        assertEquals(9, testaddProduct.getStock());
    }

    /**
     * Tests behaviour of removeStock()
     */
    @Test
    void removeStock() {
        Product testRemoveProduct = new Product();
        testRemoveProduct.setId(6L);
        testRemoveProduct.setName("Test Product");
        testRemoveProduct.setDescription("Test Description");
        testRemoveProduct.setPrice(1.0);
        testRemoveProduct.setStock(8);
        assertFalse(testRemoveProduct.removeStock(9));
        assertTrue(testRemoveProduct.removeStock(8));
        assertEquals(0, testRemoveProduct.getStock());
    }
}
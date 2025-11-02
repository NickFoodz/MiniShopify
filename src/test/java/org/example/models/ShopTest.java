package org.example.models;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Test class for Shop model
 * @author Nick Fuda
 * @version 1.0
 */
class ShopTest {

    private Shop testShop;
    private Merchant testMerchant;

    @BeforeEach
    void setUp() {
        testShop = new Shop();
        testShop.setId(1L);
        testShop.setName("Test Shop");
        testMerchant = new Merchant();
        testShop.setMerchant(testMerchant);



    }

    @AfterEach
    void tearDown() {
    }

    /**
     * Tests getId() in Shop class
     */
    @Test
    void getId() {
        assertEquals(1L, testShop.getId());
    }

    /**
     * Tests setId() in Shop class
     */
    @Test
    void setId() {
        testShop.setId(2L);
        assertEquals(2L, testShop.getId());
    }

    /**
     * Tests getName() in Shop class
     */
    @Test
    void getName() {
        assertEquals("Test Shop", testShop.getName());
    }

    /**
     * Tests setName() in Shop class
     */
    @Test
    void setName() {
        testShop.setName("Test Shop 2");
        assertEquals("Test Shop 2", testShop.getName());
    }

    /**
     * Tests getCategories() in Shop class
     */
    @Test
    void getCategories() {
        List<String> testCategories = testShop.getCategories();
        assertEquals(testCategories, testShop.getCategories());
    }

    /**
     * Tests addCategory() in Shop class
     */
    @Test
    void addCategory() {
        testShop.addCategory("test");
        assertEquals(1, testShop.getCategories().size());
        assertEquals("test", testShop.getCategories().get(0));
    }

    /**
     * Tests getMerchant() in Shop class
     */
    @Test
    void getMerchant() {
        testMerchant = testShop.getMerchant();
        assertEquals(testMerchant, testShop.getMerchant());
    }

    /**
     * Tests setMerchant() in Shop class
     */
    @Test
    void setMerchant() {
        Merchant testMerchant2 = new Merchant();
        testShop.setMerchant(testMerchant2);
        assertEquals(testMerchant2, testShop.getMerchant());
    }

    /**
     * Tests getProducts() in Shop class
     */
    @Test
    void getProducts() {
        List<Product> testProducts = testShop.getProducts();
        assertEquals(testProducts, testShop.getProducts());
    }

    /**
     * Tests addProduct() in Shop class
     */
    @Test
    void addProduct() {
        Product testProduct = new Product();
        testProduct.setName("Test Product");
        testShop.addProduct(testProduct);
        assertEquals(1, testShop.getProducts().size());
        assertEquals("Test Product", testShop.getProducts().get(0).getName());

    }

    /**
     * Tests removeProduct() in Shop class
     */
    @Test
    void removeProduct() {
        List<Product> testProducts = testShop.getProducts();
        Product productTest = new Product();
        productTest.setName("Product1");
        productTest.setId(5L);
        //Add and test it is in the product list
        testShop.addProduct(productTest);
        assertTrue(testProducts.contains(productTest));

        //Test removal
        testShop.removeProduct(5);
        assertFalse(testProducts.contains(productTest));
        assertTrue(testShop.getProducts().isEmpty());


    }
}
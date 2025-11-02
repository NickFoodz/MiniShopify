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

    @Test
    void getId() {
        assertEquals(1L, testShop.getId());
    }

    @Test
    void setId() {
        testShop.setId(2L);
        assertEquals(2L, testShop.getId());
    }

    @Test
    void getName() {
        assertEquals("Test Shop", testShop.getName());
    }

    @Test
    void setName() {
        testShop.setName("Test Shop 2");
        assertEquals("Test Shop 2", testShop.getName());
    }

    @Test
    void getCategories() {
        List<String> testCategories = testShop.getCategories();
        assertEquals(testCategories, testShop.getCategories());
    }

    @Test
    void addCategory() {
        testShop.addCategory("test");
        assertEquals(1, testShop.getCategories().size());
        assertEquals("test", testShop.getCategories().get(0));
    }

    @Test
    void getMerchant() {
        testMerchant = testShop.getMerchant();
        assertEquals(testMerchant, testShop.getMerchant());
    }

    @Test
    void setMerchant() {
        Merchant testMerchant2 = new Merchant();
        testShop.setMerchant(testMerchant2);
        assertEquals(testMerchant2, testShop.getMerchant());
    }

    @Test
    void getProducts() {
        List<Product> testProducts = testShop.getProducts();
        assertEquals(testProducts, testShop.getProducts());
    }

    @Test
    void addProduct() {
        Product testProduct = new Product();
        testProduct.setName("Test Product");
        testShop.addProduct(testProduct);
        assertEquals(1, testShop.getProducts().size());
        assertEquals("Test Product", testShop.getProducts().get(0).getName());

    }

    @Test
    void removeProduct() {
        List<Product> testProducts = testShop.getProducts();
        Product productTest = new Product();
        productTest.setName("Product1");
        //Add and test it is in the product list
        testShop.addProduct(productTest);
        assertTrue(testProducts.contains(productTest));

        //Test removal
        testShop.removeProduct("Product1");
        assertFalse(testProducts.contains(productTest));
        assertTrue(testShop.getProducts().isEmpty());

        //This test can fail if one or more shops do NOT have a name field (e.g. a null name field)

    }
}
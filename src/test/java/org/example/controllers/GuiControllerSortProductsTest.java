package org.example.controllers;

import org.example.models.Product;
import org.example.models.Shop;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the sortProducts() helper method in GuiController
 * Tests sorting logic in isolation
 */
class GuiControllerSortProductsTest {

    private GuiController controller;
    private Shop testShop;
    private List<Product> products;

    @BeforeEach
    void setUp() {
        controller = new GuiController(null, null, null, null);
        testShop = new Shop();
        testShop.setName("Test Shop");

        // Create test products with varying attributes
        products = new ArrayList<>();

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Zebra Product");
        product1.setPrice(100.00);
        product1.setStock(10);
        product1.setShop(testShop);
        products.add(product1);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Alpha Product");
        product2.setPrice(50.00);
        product2.setStock(5);
        product2.setShop(testShop);
        products.add(product2);

        Product product3 = new Product();
        product3.setId(3L);
        product3.setName("Beta Product");
        product3.setPrice(75.00);
        product3.setStock(20);
        product3.setShop(testShop);
        products.add(product3);
    }

    /**
     * Test sorting by name ascending (A-Z)
     */
    @Test
    void testSortByNameAscending() {
        List<Product> sorted = invokePrivateSortMethod(products, "name-asc");

        assertEquals("Alpha Product", sorted.get(0).getName());
        assertEquals("Beta Product", sorted.get(1).getName());
        assertEquals("Zebra Product", sorted.get(2).getName());
    }

    /**
     * Test sorting by name descending (Z-A)
     */
    @Test
    void testSortByNameDescending() {
        List<Product> sorted = invokePrivateSortMethod(products, "name-desc");

        assertEquals("Zebra Product", sorted.get(0).getName());
        assertEquals("Beta Product", sorted.get(1).getName());
        assertEquals("Alpha Product", sorted.get(2).getName());
    }

    /**
     * Test sorting by price ascending (low to high)
     */
    @Test
    void testSortByPriceAscending() {
        List<Product> sorted = invokePrivateSortMethod(products, "price-asc");

        assertEquals(50.00, sorted.get(0).getPrice(), 0.01);
        assertEquals(75.00, sorted.get(1).getPrice(), 0.01);
        assertEquals(100.00, sorted.get(2).getPrice(), 0.01);
    }

    /**
     * Test sorting by price descending (high to low)
     */
    @Test
    void testSortByPriceDescending() {
        List<Product> sorted = invokePrivateSortMethod(products, "price-desc");

        assertEquals(100.00, sorted.get(0).getPrice(), 0.01);
        assertEquals(75.00, sorted.get(1).getPrice(), 0.01);
        assertEquals(50.00, sorted.get(2).getPrice(), 0.01);
    }

    /**
     * Test sorting by stock (high to low)
     */
    @Test
    void testSortByStockDescending() {
        List<Product> sorted = invokePrivateSortMethod(products, "stock");

        assertEquals(20, sorted.get(0).getStock());
        assertEquals(10, sorted.get(1).getStock());
        assertEquals(5, sorted.get(2).getStock());
    }

    /**
     * Test sorting with empty list returns empty list
     */
    @Test
    void testSortEmptyList() {
        List<Product> emptyList = new ArrayList<>();
        List<Product> sorted = invokePrivateSortMethod(emptyList, "price-asc");

        assertNotNull(sorted);
        assertTrue(sorted.isEmpty());
    }

    /**
     * Test sorting with null list returns null
     */
    @Test
    void testSortNullList() {
        List<Product> sorted = invokePrivateSortMethod(null, "price-asc");
        assertNull(sorted);
    }

    /**
     * Test sorting with single product returns single product
     */
    @Test
    void testSortSingleProduct() {
        List<Product> singleProduct = new ArrayList<>();
        singleProduct.add(products.get(0));

        List<Product> sorted = invokePrivateSortMethod(singleProduct, "price-asc");

        assertEquals(1, sorted.size());
        assertEquals("Zebra Product", sorted.get(0).getName());
    }

    /**
     * Test invalid sort parameter defaults to name ascending
     */
    @Test
    void testInvalidSortParameterDefaultsToNameAsc() {
        List<Product> sorted = invokePrivateSortMethod(products, "invalid-sort");

        // Should default to name-asc
        assertEquals("Alpha Product", sorted.get(0).getName());
        assertEquals("Beta Product", sorted.get(1).getName());
        assertEquals("Zebra Product", sorted.get(2).getName());
    }

    /**
     * Test case insensitive sort parameter
     */
    @Test
    void testCaseInsensitiveSortParameter() {
        List<Product> sorted1 = invokePrivateSortMethod(products, "PRICE-ASC");
        List<Product> sorted2 = invokePrivateSortMethod(products, "price-asc");

        // Both should produce same result
        assertEquals(sorted1.get(0).getPrice(), sorted2.get(0).getPrice(), 0.01);
        assertEquals(sorted1.get(1).getPrice(), sorted2.get(1).getPrice(), 0.01);
        assertEquals(sorted1.get(2).getPrice(), sorted2.get(2).getPrice(), 0.01);
    }

    /**
     * Test sorting with identical prices
     */
    @Test
    void testSortingWithIdenticalPrices() {
        Product product4 = new Product();
        product4.setId(4L);
        product4.setName("Delta Product");
        product4.setPrice(50.00); // Same as product2
        product4.setStock(15);
        product4.setShop(testShop);
        products.add(product4);

        List<Product> sorted = invokePrivateSortMethod(products, "price-asc");

        // First two should both be $50
        assertEquals(50.00, sorted.get(0).getPrice(), 0.01);
        assertEquals(50.00, sorted.get(1).getPrice(), 0.01);
    }

    /**
     * Test sorting with identical names
     */
    @Test
    void testSortingWithIdenticalNames() {
        Product product4 = new Product();
        product4.setId(4L);
        product4.setName("Alpha Product"); // Same as product2
        product4.setPrice(200.00);
        product4.setStock(15);
        product4.setShop(testShop);
        products.add(product4);

        List<Product> sorted = invokePrivateSortMethod(products, "name-asc");

        // First two should both be "Alpha Product"
        assertEquals("Alpha Product", sorted.get(0).getName());
        assertEquals("Alpha Product", sorted.get(1).getName());
    }

    /**
     * Test sorting with identical stock levels
     */
    @Test
    void testSortingWithIdenticalStock() {
        Product product4 = new Product();
        product4.setId(4L);
        product4.setName("Gamma Product");
        product4.setPrice(150.00);
        product4.setStock(10); // Same as product1
        product4.setShop(testShop);
        products.add(product4);

        List<Product> sorted = invokePrivateSortMethod(products, "stock");

        // Should have 20, then two products with 10
        assertEquals(20, sorted.get(0).getStock());
        assertEquals(10, sorted.get(1).getStock());
        assertEquals(10, sorted.get(2).getStock());
    }

    /**
     * Test sorting with zero price
     */
    @Test
    void testSortingWithZeroPrice() {
        Product freeProduct = new Product();
        freeProduct.setId(4L);
        freeProduct.setName("Free Product");
        freeProduct.setPrice(0.00);
        freeProduct.setStock(100);
        freeProduct.setShop(testShop);
        products.add(freeProduct);

        List<Product> sorted = invokePrivateSortMethod(products, "price-asc");

        assertEquals(0.00, sorted.get(0).getPrice(), 0.01);
    }

    /**
     * Test sorting with zero stock
     */
    @Test
    void testSortingWithZeroStock() {
        Product outOfStock = new Product();
        outOfStock.setId(4L);
        outOfStock.setName("Out of Stock Product");
        outOfStock.setPrice(99.99);
        outOfStock.setStock(0);
        outOfStock.setShop(testShop);
        products.add(outOfStock);

        List<Product> sorted = invokePrivateSortMethod(products, "stock");

        // Zero stock should be last
        assertEquals(0, sorted.get(sorted.size() - 1).getStock());
    }

    /**
     * Test sorting doesn't modify original list
     */
    @Test
    void testSortingDoesNotModifyOriginalList() {
        List<Product> original = new ArrayList<>(products);
        String originalFirstName = original.get(0).getName();

        invokePrivateSortMethod(products, "name-asc");

        // Original list order should be unchanged
        assertEquals(originalFirstName, products.get(0).getName());
    }

    /**
     * Test sorting with very large price difference
     */
    @Test
    void testSortingWithLargePriceDifference() {
        Product expensive = new Product();
        expensive.setId(4L);
        expensive.setName("Expensive Product");
        expensive.setPrice(9999999.99);
        expensive.setStock(1);
        expensive.setShop(testShop);
        products.add(expensive);

        List<Product> sorted = invokePrivateSortMethod(products, "price-desc");

        assertEquals(9999999.99, sorted.get(0).getPrice(), 0.01);
    }

    /**
     * Test null sort parameter defaults to name-asc
     */
    @Test
    void testNullSortParameterDefaultsToNameAsc() {
        List<Product> sorted = invokePrivateSortMethod(products, null);

        // Should default to name-asc
        assertEquals("Alpha Product", sorted.get(0).getName());
    }

    /**
     * Helper method to invoke private sortProducts method using reflection
     */
    @SuppressWarnings("unchecked")
    private List<Product> invokePrivateSortMethod(List<Product> products, String sortBy) {
        return (List<Product>) ReflectionTestUtils.invokeMethod(
                controller,
                "sortProducts",
                products,
                sortBy
        );
    }
}
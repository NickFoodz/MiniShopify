package org.example.repository;

import org.example.ShopAppApplication;
import org.example.models.Product;
import org.example.models.Shop;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for ShopRepository
 * Tests repository operations with actual database
 */
@SpringBootTest(classes = ShopAppApplication.class)
class ShopRepositoryIntegrationTest {

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private ProductRepository productRepository;

    @AfterEach
    void cleanup() {
        productRepository.deleteAll();
        shopRepository.deleteAll();
    }

    @Test
/**
 * Tests the behavior of testSaveAndFindShop().
 */
    void testSaveAndFindShop() {
        Shop shop = new Shop();
        shop.setName("Tech Store");

        Shop saved = shopRepository.save(shop);

        // Validate expected outcomes
        assertNotNull(saved.getId());
        // Validate expected outcomes
        assertEquals("Tech Store", saved.getName());

        Optional<Shop> found = shopRepository.findById(saved.getId());
        // Validate expected outcomes
        assertTrue(found.isPresent());
        // Validate expected outcomes
        assertEquals("Tech Store", found.get().getName());
    }

    @Test
/**
 * Tests the behavior of testShopWithProducts().
 */
    void testShopWithProducts() {
        Shop shop = new Shop();
        shop.setName("Electronics Shop");

        Product product1 = new Product();
        product1.setName("Laptop");
        product1.setPrice(999.99);
        product1.setStock(10);

        Product product2 = new Product();
        product2.setName("Mouse");
        product2.setPrice(29.99);
        product2.setStock(50);

        shop.addProduct(product1);
        shop.addProduct(product2);

        Shop saved = shopRepository.save(shop);

        Optional<Shop> found = shopRepository.findById(saved.getId());
        // Validate expected outcomes
        assertTrue(found.isPresent());
        // Validate expected outcomes
        assertEquals(2, found.get().getProducts().size());
        // Validate expected outcomes
        assertEquals("Laptop", found.get().getProducts().get(0).getName());
    }

    @Test
/**
 * Tests the behavior of testShopWithCategories().
 */
    void testShopWithCategories() {
        Shop shop = new Shop();
        shop.setName("Multi-Category Store");
        shop.addCategory("Electronics");
        shop.addCategory("Clothing");

        Shop saved = shopRepository.save(shop);

        Optional<Shop> found = shopRepository.findById(saved.getId());
        // Validate expected outcomes
        assertTrue(found.isPresent());
        // Validate expected outcomes
        assertEquals(2, found.get().getCategories().size());
        // Validate expected outcomes
        assertTrue(found.get().getCategories().contains("Electronics"));
        // Validate expected outcomes
        assertTrue(found.get().getCategories().contains("Clothing"));
    }

    @Test
/**
 * Tests the behavior of testDeleteShopCascadesProducts().
 */
    void testDeleteShopCascadesProducts() {
        Shop shop = new Shop();
        shop.setName("Test Shop");

        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(50.0);
        product.setStock(5);

        shop.addProduct(product);
        Shop saved = shopRepository.save(shop);
        Long productId = saved.getProducts().get(0).getId();

        shopRepository.deleteById(saved.getId());

        // Validate expected outcomes
        assertFalse(shopRepository.findById(saved.getId()).isPresent());
        // Validate expected outcomes
        assertFalse(productRepository.findById(productId).isPresent());
    }

    @Test
/**
 * Tests the behavior of testFindAllShops().
 */
    void testFindAllShops() {
        Shop shop1 = new Shop();
        shop1.setName("Shop 1");

        Shop shop2 = new Shop();
        shop2.setName("Shop 2");

        shopRepository.save(shop1);
        shopRepository.save(shop2);

        Iterable<Shop> shops = shopRepository.findAll();
        long count = 0;
        for (Shop s : shops) {
            count++;
        }

        // Validate expected outcomes
        assertEquals(2, count);
    }
}
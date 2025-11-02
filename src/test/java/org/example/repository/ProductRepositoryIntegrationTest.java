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
 * Integration tests for ProductRepository
 * Tests product CRUD operations with database
 */
@SpringBootTest(classes = ShopAppApplication.class)
class ProductRepositoryIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ShopRepository shopRepository;

    @AfterEach
    void cleanup() {
        productRepository.deleteAll();
        shopRepository.deleteAll();
    }

    @Test
/**
 * Tests the behavior of testSaveAndFindProduct().
 */
    void testSaveAndFindProduct() {
        Shop shop = new Shop();
        shop.setName("Test Shop");
        Shop savedShop = shopRepository.save(shop);

        Product product = new Product();
        product.setName("iPhone");
        product.setDescription("Latest smartphone");
        product.setPrice(1099.99);
        product.setStock(25);
        product.setShop(savedShop);

        Product saved = productRepository.save(product);

        // Validate expected outcomes
        assertNotNull(saved.getId());
        // Validate expected outcomes
        assertEquals("iPhone", saved.getName());
        // Validate expected outcomes
        assertEquals(1099.99, saved.getPrice());

        Optional<Product> found = productRepository.findById(saved.getId());
        // Validate expected outcomes
        assertTrue(found.isPresent());
        // Validate expected outcomes
        assertEquals("Latest smartphone", found.get().getDescription());
        // Validate expected outcomes
        assertEquals(25, found.get().getStock());
    }

    @Test
/**
 * Tests the behavior of testUpdateProduct().
 */
    void testUpdateProduct() {
        Shop shop = new Shop();
        shop.setName("Test Shop");
        Shop savedShop = shopRepository.save(shop);

        Product product = new Product();
        product.setName("Keyboard");
        product.setPrice(79.99);
        product.setStock(100);
        product.setShop(savedShop);

        Product saved = productRepository.save(product);

        saved.setPrice(69.99);
        saved.setStock(90);
        productRepository.save(saved);

        Optional<Product> updated = productRepository.findById(saved.getId());
        // Validate expected outcomes
        assertTrue(updated.isPresent());
        // Validate expected outcomes
        assertEquals(69.99, updated.get().getPrice());
        // Validate expected outcomes
        assertEquals(90, updated.get().getStock());
    }

    @Test
/**
 * Tests the behavior of testDeleteProduct().
 */
    void testDeleteProduct() {
        Shop shop = new Shop();
        shop.setName("Test Shop");

        Product product = new Product();
        product.setName("Monitor");
        product.setPrice(299.99);
        product.setStock(15);

        shop.addProduct(product);
        Shop savedShop = shopRepository.save(shop);

        Long productId = savedShop.getProducts().get(0).getId();

        savedShop.getProducts().remove(product);
        shopRepository.save(savedShop);

        productRepository.deleteById(productId);

        // Validate expected outcomes
        assertFalse(productRepository.findById(productId).isPresent());
    }

    @Test
/**
 * Tests the behavior of testProductShopRelationship().
 */
    void testProductShopRelationship() {
        Shop shop = new Shop();
        shop.setName("Gaming Store");
        Shop savedShop = shopRepository.save(shop);

        Product product = new Product();
        product.setName("PS5");
        product.setPrice(499.99);
        product.setStock(5);
        product.setShop(savedShop);

        Product saved = productRepository.save(product);

        Optional<Product> found = productRepository.findById(saved.getId());
        // Validate expected outcomes
        assertTrue(found.isPresent());
        // Validate expected outcomes
        assertNotNull(found.get().getShop());
        // Validate expected outcomes
        assertEquals("Gaming Store", found.get().getShop().getName());
    }
}
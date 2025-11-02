package org.example.controllers;

import org.example.ShopAppApplication;
import org.example.models.Product;
import org.example.models.Shop;
import org.example.repository.ProductRepository;
import org.example.repository.ShopRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for GuiController
 * Tests HTTP endpoints and view rendering
 */
@SpringBootTest(classes = ShopAppApplication.class)
@AutoConfigureMockMvc
class GuiControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

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
 * Tests the behavior of testHomePage().
 */
    void testHomePage() throws Exception {
        mockMvc.perform(get("/gui/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(content().string(containsString("MiniShopify Dashboard")));
    }

    @Test
/**
 * Tests the behavior of testShopsPage().
 */
    void testShopsPage() throws Exception {
        Shop shop = new Shop();
        shop.setName("Test Shop");
        shopRepository.save(shop);

        mockMvc.perform(get("/gui/shops"))
                .andExpect(status().isOk())
                .andExpect(view().name("shops"))
                .andExpect(model().attributeExists("shops"));
    }

    @Test
/**
 * Tests the behavior of testAddShop().
 */
    void testAddShop() throws Exception {
        mockMvc.perform(post("/gui/shops")
                        .param("name", "New Electronics Store"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gui/shops"));

        Iterable<Shop> shops = shopRepository.findAll();
        long count = 0;
        Shop foundShop = null;
        for (Shop s : shops) {
            count++;
            foundShop = s;
        }

        // Validate expected outcomes
        assertEquals(1, count);
        // Validate expected outcomes
        assertEquals("New Electronics Store", foundShop.getName());
    }

    @Test
/**
 * Tests the behavior of testAddProductForm().
 */
    void testAddProductForm() throws Exception {
        mockMvc.perform(get("/gui/add-product"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-product"))
                .andExpect(content().string(containsString("Add a Product")));
    }

    @Test
/**
 * Tests the behavior of testAddProductSuccess().
 */
    void testAddProductSuccess() throws Exception {
        Shop shop = new Shop();
        shop.setName("Tech Store");
        Shop savedShop = shopRepository.save(shop);

        mockMvc.perform(post("/gui/add-product")
                        .param("name", "Laptop")
                        .param("description", "Gaming laptop")
                        .param("price", "1299.99")
                        .param("stock", "15")
                        .param("shopID", savedShop.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gui/shops"));

        Iterable<Product> products = productRepository.findAll();
        long count = 0;
        Product foundProduct = null;
        for (Product p : products) {
            count++;
            foundProduct = p;
        }

        // Validate expected outcomes
        assertEquals(1, count);
        // Validate expected outcomes
        assertEquals("Laptop", foundProduct.getName());
        // Validate expected outcomes
        assertEquals(1299.99, foundProduct.getPrice(), 0.01);
        // Validate expected outcomes
        assertEquals(15, foundProduct.getStock());
    }

    @Test
/**
 * Tests the behavior of testAddProductInvalidShop().
 */
    void testAddProductInvalidShop() throws Exception {
        mockMvc.perform(post("/gui/add-product")
                        .param("name", "Laptop")
                        .param("description", "Gaming laptop")
                        .param("price", "1299.99")
                        .param("stock", "15")
                        .param("shopID", "999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/error"));

        Iterable<Product> products = productRepository.findAll();
        long count = 0;
        for (Product p : products) {
            count++;
        }

        // Validate expected outcomes
        assertEquals(0, count);
    }

    @Test
/**
 * Tests the behavior of testShopsPageDisplaysProducts().
 */
    void testShopsPageDisplaysProducts() throws Exception {
        Shop shop = new Shop();
        shop.setName("Electronics");

        Product product1 = new Product();
        product1.setName("Phone");
        product1.setPrice(599.99);
        product1.setStock(20);

        Product product2 = new Product();
        product2.setName("Tablet");
        product2.setPrice(399.99);
        product2.setStock(10);

        shop.addProduct(product1);
        shop.addProduct(product2);
        shopRepository.save(shop);

        mockMvc.perform(get("/gui/shops"))
                .andExpect(status().isOk())
                .andExpect(view().name("shops"))
                .andExpect(content().string(containsString("Phone")))
                .andExpect(content().string(containsString("Tablet")));
    }
}
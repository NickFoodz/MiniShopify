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
import org.springframework.security.test.context.support.WithMockUser;
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


/**
 *Tests will need to be refactored (add product doesn't exist anymore)
 * Tests the behavior of testAddProductForm().
 *
 * @Test
 * void testAddProductForm() throws Exception {
 *         mockMvc.perform(get("/gui/add-product"))
 *                 .andExpect(status().isOk())
 *                 .andExpect(view().name("add-product"))
 *                 .andExpect(content().string(containsString("Add a Product")));
 *     }
 */

/**
 * Tests will need to be refactored (add product doesn't exist anymore)
 * Tests the behavior of testAddProductSuccess().
 *
 * @Test
 * void testAddProductSuccess() throws Exception {
 *         Shop shop = new Shop();
 *         shop.setName("Tech Store");
 *         Shop savedShop = shopRepository.save(shop);
 *
 *         mockMvc.perform(post("/gui/add-product")
 *                         .param("name", "Laptop")
 *                         .param("description", "Gaming laptop")
 *                         .param("price", "1299.99")
 *                         .param("stock", "15")
 *                         .param("shopID", savedShop.getId().toString()))
 *                 .andExpect(status().is3xxRedirection())
 *                 .andExpect(redirectedUrl("/gui/shops"));
 *
 *         Iterable<Product> products = productRepository.findAll();
 *         long count = 0;
 *         Product foundProduct = null;
 *         for (Product p : products) {
 *             count++;
 *             foundProduct = p;
 *         }
 *
 *         // Validate expected outcomes
 *         assertEquals(1, count);
 *         // Validate expected outcomes
 *         assertEquals("Laptop", foundProduct.getName());
 *         // Validate expected outcomes
 *         assertEquals(1299.99, foundProduct.getPrice(), 0.01);
 *         // Validate expected outcomes
 *         assertEquals(15, foundProduct.getStock());
 *     }
 */



/**
 *
 * Tests the behavior of testAddProductInvalidShop().
 * Tests will need to be refactored (add product doesn't exist anymore)
 *@Test
 * void testAddProductInvalidShop() throws Exception {
 *         mockMvc.perform(post("/gui/add-product")
 *                         .param("name", "Laptop")
 *                         .param("description", "Gaming laptop")
 *                         .param("price", "1299.99")
 *                         .param("stock", "15")
 *                         .param("shopID", "999"))
 *                 .andExpect(status().is3xxRedirection())
 *                 .andExpect(redirectedUrl("/gui/custom-error"));
 *
 *         Iterable<Product> products = productRepository.findAll();
 *         long count = 0;
 *         for (Product p : products) {
 *             count++;
 *         }
 *
 *         // Validate expected outcomes
 *         assertEquals(0, count);
 *     }
 */


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

    /**
     * Tests if removing a product was successful
     */
    @Test
    @WithMockUser(username = "merchant1", roles = {"MERCHANT"})
    void testRemoveProductSuccess() throws Exception {
        // Create and save a shop
        Shop shop = new Shop();
        shop.setName("Electronics Store");
        Shop savedShop = shopRepository.save(shop);

        // Create and add a product to the shop
        Product product = new Product();
        product.setName("Laptop");
        product.setDescription("Gaming laptop");
        product.setPrice(1299.99);
        product.setStock(10);
        product.setShop(savedShop);
        savedShop.addProduct(product);
        shopRepository.save(savedShop);

        // Get the product ID for removal
        Product savedProduct = productRepository.findAll().iterator().next();

        // Perform POST request to remove the product
        mockMvc.perform(post("/gui/remove-product")
                        .param("productId", savedProduct.getId().toString())
                        .param("shopId", savedShop.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gui/shops"));

        // Verify the product was deleted from repository
        assertFalse(productRepository.existsById(savedProduct.getId()));

        // Verify the shop still exists
        assertTrue(shopRepository.existsById(savedShop.getId()));

        // Verify the product was removed from shop's product list
        Shop updatedShop = shopRepository.findById(savedShop.getId()).get();
        assertEquals(0, updatedShop.getProducts().size());
    }


    /**
     * Tests the behavior of removeProduct() - product not found
     */
    @Test
    @WithMockUser(username = "merchant1", roles = {"MERCHANT"})
    void testRemoveProductNotFound() throws Exception {
        // Create and save a shop
        Shop shop = new Shop();
        shop.setName("Electronics Store");
        Shop savedShop = shopRepository.save(shop);

        // Attempt to remove a non-existent product (ID 999)
        mockMvc.perform(post("/gui/remove-product")
                        .param("productId", "999")
                        .param("shopId", savedShop.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gui/custom-error"));
    }

    /**
     * Tests the behavior of removeProduct() - shop not found
     */
    @Test
    @WithMockUser(username = "merchant1", roles = {"MERCHANT"})
    void testRemoveProductShopNotFound() throws Exception {
        // Attempt to remove a product from a non-existent shop (ID 999)
        mockMvc.perform(post("/gui/remove-product")
                        .param("productId", "1")
                        .param("shopId", "999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gui/custom-error"));
    }

    /**
     * Tests the behavior of removeProduct() - removes correct product when multiple exist
     */
    @Test
    @WithMockUser(username = "merchant1", roles = {"MERCHANT"})
    void testRemoveProductMultipleProducts() throws Exception {
        // Create and save a shop
        Shop shop = new Shop();
        shop.setName("Tech Store");

        // Create multiple products
        Product product1 = new Product();
        product1.setName("Laptop");
        product1.setPrice(1299.99);
        product1.setStock(10);

        Product product2 = new Product();
        product2.setName("Mouse");
        product2.setPrice(29.99);
        product2.setStock(50);

        // Add both products to shop
        shop.addProduct(product1);
        shop.addProduct(product2);
        Shop savedShop = shopRepository.save(shop);

        // Get the first product's ID
        Product laptopProduct = productRepository.findAll().iterator().next();

        // Remove only the laptop
        mockMvc.perform(post("/gui/remove-product")
                        .param("productId", laptopProduct.getId().toString())
                        .param("shopId", savedShop.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gui/shops"));

        // Verify only one product remains
        Shop updatedShop = shopRepository.findById(savedShop.getId()).get();
        assertEquals(1, updatedShop.getProducts().size());

        // Verify the correct product was removed
        assertFalse(productRepository.existsById(laptopProduct.getId()));

        // Verify the other product still exists
        long remainingProductCount = 0;
        for (Product p : productRepository.findAll()) {
            remainingProductCount++;
        }
        assertEquals(1, remainingProductCount);
    }

    /**
     * Tests the behavior of removeShop() - successful removal
     */
    @Test
    @WithMockUser(username = "merchant1", roles = {"MERCHANT"})
    void testRemoveShopSuccess() throws Exception {
        // Create and save a shop
        Shop shop = new Shop();
        shop.setName("Clothing Store");
        Shop savedShop = shopRepository.save(shop);

        Long shopId = savedShop.getId();

        // Perform POST request to remove the shop
        mockMvc.perform(post("/gui/remove-shop")
                        .param("shopId", shopId.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gui/shops"));

        // Verify the shop was deleted from repository
        assertFalse(shopRepository.existsById(shopId));
    }

    /**
     * Tests the behavior of removeShop() - shop not found
     */
    @Test
    @WithMockUser(username = "merchant1", roles = {"MERCHANT"})
    void testRemoveShopNotFound() throws Exception {
        // Attempt to remove a non-existent shop (ID 999)
        mockMvc.perform(post("/gui/remove-shop")
                        .param("shopId", "999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gui/custom-error"));
    }

    /**
     * Tests the behavior of removeShop() - shop with products is removed (cascade delete)
     */
    @Test
    @WithMockUser(username = "merchant1", roles = {"MERCHANT"})
    void testRemoveShopWithProducts() throws Exception {
        // Create and save a shop
        Shop shop = new Shop();
        shop.setName("Game Store");

        // Create and add products to the shop
        Product product1 = new Product();
        product1.setName("PlayStation 5");
        product1.setPrice(499.99);
        product1.setStock(5);

        Product product2 = new Product();
        product2.setName("Xbox Series X");
        product2.setPrice(499.99);
        product2.setStock(3);

        shop.addProduct(product1);
        shop.addProduct(product2);
        Shop savedShop = shopRepository.save(shop);

        Long shopId = savedShop.getId();

        // Get product IDs before deletion
        Long product1Id = null;
        Long product2Id = null;
        for (Product p : productRepository.findAll()) {
            if (p.getName().equals("PlayStation 5")) {
                product1Id = p.getId();
            } else if (p.getName().equals("Xbox Series X")) {
                product2Id = p.getId();
            }
        }

        // Remove the shop
        mockMvc.perform(post("/gui/remove-shop")
                        .param("shopId", shopId.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gui/shops"));

        // Verify the shop was deleted
        assertFalse(shopRepository.existsById(shopId));

        // Verify associated products were also deleted (cascade delete)
        assertFalse(productRepository.existsById(product1Id));
        assertFalse(productRepository.existsById(product2Id));
    }

    /**
     * Tests the behavior of removeShop() - correct shop removed when multiple exist
     */
    @Test
    @WithMockUser(username = "merchant1", roles = {"MERCHANT"})
    void testRemoveShopMultipleShops() throws Exception {
        // Create and save multiple shops
        Shop shop1 = new Shop();
        shop1.setName("Shop A");
        Shop savedShop1 = shopRepository.save(shop1);

        Shop shop2 = new Shop();
        shop2.setName("Shop B");
        Shop savedShop2 = shopRepository.save(shop2);

        // Remove only shop1
        mockMvc.perform(post("/gui/remove-shop")
                        .param("shopId", savedShop1.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gui/shops"));

        // Verify shop1 was deleted
        assertFalse(shopRepository.existsById(savedShop1.getId()));

        // Verify shop2 still exists
        assertTrue(shopRepository.existsById(savedShop2.getId()));

        // Verify only one shop remains
        long shopCount = 0;
        for (Shop s : shopRepository.findAll()) {
            shopCount++;
        }
        assertEquals(1, shopCount);
    }


}
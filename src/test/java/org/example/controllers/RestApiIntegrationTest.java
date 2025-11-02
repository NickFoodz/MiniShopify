package org.example.controllers;

import org.example.ShopAppApplication;
import org.example.models.Merchant;
import org.example.models.Product;
import org.example.models.Shop;
import org.example.repository.MerchantRepository;
import org.example.repository.ProductRepository;
import org.example.repository.ShopRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for REST API endpoints
 * Tests Spring Data REST auto-generated endpoints
 */
@SpringBootTest(classes = ShopAppApplication.class)
@AutoConfigureMockMvc
class RestApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MerchantRepository merchantRepository;

    @AfterEach
    void cleanup() {
        productRepository.deleteAll();
        shopRepository.deleteAll();
        merchantRepository.deleteAll();
    }

    @Test
/**
 * Tests the behavior of testGetAllShops().
 */
    void testGetAllShops() throws Exception {
        Shop shop1 = new Shop();
        shop1.setName("Shop 1");

        Shop shop2 = new Shop();
        shop2.setName("Shop 2");

        shopRepository.save(shop1);
        shopRepository.save(shop2);

        mockMvc.perform(get("/shops"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$._embedded.shops", hasSize(2)));
    }

    @Test
/**
 * Tests the behavior of testGetAllProducts().
 */
    void testGetAllProducts() throws Exception {
        Shop shop = new Shop();
        shop.setName("Electronics");
        Shop savedShop = shopRepository.save(shop);

        Product product = new Product();
        product.setName("Laptop");
        product.setPrice(999.99);
        product.setStock(10);
        product.setShop(savedShop);

        productRepository.save(product);

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$._embedded.products", hasSize(1)))
                .andExpect(jsonPath("$._embedded.products[0].name", is("Laptop")));
    }

    @Test
/**
 * Tests the behavior of testGetAllMerchants().
 */
    void testGetAllMerchants() throws Exception {
        Merchant merchant = new Merchant();
        merchant.setName("John Doe");
        merchant.setEmail("john@example.com");

        merchantRepository.save(merchant);

        mockMvc.perform(get("/merchants"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$._embedded.merchants", hasSize(1)))
                .andExpect(jsonPath("$._embedded.merchants[0].name", is("John Doe")));
    }

    @Test
/**
 * Tests the behavior of testGetShopById().
 */
    void testGetShopById() throws Exception {
        Shop shop = new Shop();
        shop.setName("Test Shop");
        Shop saved = shopRepository.save(shop);

        mockMvc.perform(get("/shops/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Test Shop")));
    }

    @Test
/**
 * Tests the behavior of testGetProductById().
 */
    void testGetProductById() throws Exception {
        Shop shop = new Shop();
        shop.setName("Electronics");
        Shop savedShop = shopRepository.save(shop);

        Product product = new Product();
        product.setName("Mouse");
        product.setDescription("Wireless mouse");
        product.setPrice(29.99);
        product.setStock(50);
        product.setShop(savedShop);

        Product saved = productRepository.save(product);

        mockMvc.perform(get("/products/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Mouse")))
                .andExpect(jsonPath("$.description", is("Wireless mouse")))
                .andExpect(jsonPath("$.price", is(29.99)))
                .andExpect(jsonPath("$.stock", is(50)));
    }

    @Test
/**
 * Tests the behavior of testCreateShopViaRestApi().
 */
    void testCreateShopViaRestApi() throws Exception {
        String shopJson = "{\"name\": \"New API Shop\"}";

        String location = mockMvc.perform(post("/shops")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(shopJson))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn()
                .getResponse()
                .getHeader("Location");

        // Verify the shop was created by fetching it
        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("New API Shop")));
    }

    @Test
/**
 * Tests the behavior of testGetNonExistentShop().
 */
    void testGetNonExistentShop() throws Exception {
        mockMvc.perform(get("/shops/999"))
                .andExpect(status().isNotFound());
    }
}
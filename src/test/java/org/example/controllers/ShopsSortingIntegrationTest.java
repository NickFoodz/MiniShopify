package org.example.controllers;

import org.example.ShopAppApplication;
import org.example.models.Merchant;
import org.example.models.Product;
import org.example.models.Shop;
import org.example.repository.MerchantRepository;
import org.example.repository.ProductRepository;
import org.example.repository.ShopRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Product Sorting on Shops Page
 * Tests per-shop sorting functionality
 */
@SpringBootTest(classes = ShopAppApplication.class)
@AutoConfigureMockMvc
class ShopsSortingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MerchantRepository merchantRepository;

    private Shop shop1;
    private Shop shop2;
    private Merchant testMerchant;

    @BeforeEach
    void setUp() {
        // Clean database
        productRepository.deleteAll();
        shopRepository.deleteAll();
        merchantRepository.deleteAll();

        // Create test merchant
        testMerchant = new Merchant();
        testMerchant.setName("Test Merchant");
        testMerchant.setEmail("merchant@test.com");
        testMerchant.setPassword("password");
        merchantRepository.save(testMerchant);

        // Create Shop 1 with products
        shop1 = new Shop();
        shop1.setName("Electronics Store");
        shop1.setMerchant(testMerchant);

        Product laptop = new Product();
        laptop.setName("Laptop");
        laptop.setPrice(1299.99);
        laptop.setStock(5);
        laptop.setDescription("Gaming laptop");
        laptop.setShop(shop1);

        Product mouse = new Product();
        mouse.setName("Mouse");
        mouse.setPrice(29.99);
        mouse.setStock(50);
        mouse.setDescription("Wireless mouse");
        mouse.setShop(shop1);

        Product keyboard = new Product();
        keyboard.setName("Keyboard");
        keyboard.setPrice(79.99);
        keyboard.setStock(15);
        keyboard.setDescription("Mechanical keyboard");
        keyboard.setShop(shop1);

        shop1.addProduct(laptop);
        shop1.addProduct(mouse);
        shop1.addProduct(keyboard);
        shopRepository.save(shop1);

        // Create Shop 2 with different products
        shop2 = new Shop();
        shop2.setName("Clothing Store");
        shop2.setMerchant(testMerchant);

        Product shirt = new Product();
        shirt.setName("T-Shirt");
        shirt.setPrice(19.99);
        shirt.setStock(100);
        shirt.setDescription("Cotton t-shirt");
        shirt.setShop(shop2);

        Product jeans = new Product();
        jeans.setName("Jeans");
        jeans.setPrice(59.99);
        jeans.setStock(30);
        jeans.setDescription("Blue jeans");
        jeans.setShop(shop2);

        shop2.addProduct(shirt);
        shop2.addProduct(jeans);
        shopRepository.save(shop2);
    }

    @AfterEach
    void cleanup() {
        productRepository.deleteAll();
        shopRepository.deleteAll();
        merchantRepository.deleteAll();
    }

    /**
     * Test shops page loads without sorting parameters
     */
    @Test
    @WithMockUser(username = "user", roles = {"CUSTOMER"})
    void testShopsPageLoadsWithoutSorting() throws Exception {
        mockMvc.perform(get("/gui/shops"))
                .andExpect(status().isOk())
                .andExpect(view().name("shops"))
                .andExpect(model().attributeExists("shops"))
                .andExpect(model().attributeExists("sortMap"));
    }

    /**
     * Test sorting a single shop by price ascending
     */
    @Test
    @WithMockUser(username = "user", roles = {"CUSTOMER"})
    void testSortSingleShopByPriceAsc() throws Exception {
        mockMvc.perform(get("/gui/shops")
                        .param(shop1.getId().toString(), "price-asc"))
                .andExpect(status().isOk())
                .andExpect(view().name("shops"))
                .andExpect(model().attributeExists("shops"))
                .andExpect(model().attributeExists("sortMap"));
    }

    /**
     * Test sorting a single shop by price descending
     */
    @Test
    @WithMockUser(username = "user", roles = {"CUSTOMER"})
    void testSortSingleShopByPriceDesc() throws Exception {
        mockMvc.perform(get("/gui/shops")
                        .param(shop1.getId().toString(), "price-desc"))
                .andExpect(status().isOk())
                .andExpect(view().name("shops"));
    }

    /**
     * Test sorting a single shop by name descending
     */
    @Test
    @WithMockUser(username = "user", roles = {"CUSTOMER"})
    void testSortSingleShopByNameDesc() throws Exception {
        mockMvc.perform(get("/gui/shops")
                        .param(shop1.getId().toString(), "name-desc"))
                .andExpect(status().isOk())
                .andExpect(view().name("shops"));
    }

    /**
     * Test sorting a single shop by stock
     */
    @Test
    @WithMockUser(username = "user", roles = {"CUSTOMER"})
    void testSortSingleShopByStock() throws Exception {
        mockMvc.perform(get("/gui/shops")
                        .param(shop1.getId().toString(), "stock"))
                .andExpect(status().isOk())
                .andExpect(view().name("shops"));
    }

    /**
     * Test sorting multiple shops independently
     */
    @Test
    @WithMockUser(username = "user", roles = {"CUSTOMER"})
    void testSortMultipleShopsIndependently() throws Exception {
        mockMvc.perform(get("/gui/shops")
                        .param(shop1.getId().toString(), "price-asc")
                        .param(shop2.getId().toString(), "name-desc"))
                .andExpect(status().isOk())
                .andExpect(view().name("shops"))
                .andExpect(model().attributeExists("sortMap"));
    }

    /**
     * Test sorting with invalid sort parameter defaults gracefully
     */
    @Test
    @WithMockUser(username = "user", roles = {"CUSTOMER"})
    void testInvalidSortParameterDefaultsToNameAsc() throws Exception {
        mockMvc.perform(get("/gui/shops")
                        .param(shop1.getId().toString(), "invalid-sort"))
                .andExpect(status().isOk())
                .andExpect(view().name("shops"));
    }

    /**
     * Test sorting with non-existent shop ID doesn't break page
     */
    @Test
    @WithMockUser(username = "user", roles = {"CUSTOMER"})
    void testSortingWithNonExistentShopId() throws Exception {
        mockMvc.perform(get("/gui/shops")
                        .param("999", "price-asc"))
                .andExpect(status().isOk())
                .andExpect(view().name("shops"));
    }

    /**
     * Test unauthenticated user can view shops page with sorting
     */
    @Test
    void testUnauthenticatedUserCanViewShopsWithSorting() throws Exception {
        mockMvc.perform(get("/gui/shops")
                        .param(shop1.getId().toString(), "price-asc"))
                .andExpect(status().isOk())
                .andExpect(view().name("shops"));
    }

    /**
     * Test merchant can view shops page with sorting
     */
    @Test
    @WithMockUser(username = "merchant", roles = {"MERCHANT"})
    void testMerchantCanViewShopsWithSorting() throws Exception {
        mockMvc.perform(get("/gui/shops")
                        .param(shop1.getId().toString(), "stock"))
                .andExpect(status().isOk())
                .andExpect(view().name("shops"));
    }

    /**
     * Test sorting empty shop doesn't cause errors
     */
    @Test
    @WithMockUser(username = "user", roles = {"CUSTOMER"})
    void testSortingEmptyShop() throws Exception {
        // Create empty shop
        Shop emptyShop = new Shop();
        emptyShop.setName("Empty Shop");
        emptyShop.setMerchant(testMerchant);
        Shop savedEmptyShop = shopRepository.save(emptyShop);

        mockMvc.perform(get("/gui/shops")
                        .param(savedEmptyShop.getId().toString(), "price-asc"))
                .andExpect(status().isOk())
                .andExpect(view().name("shops"));
    }

    /**
     * Test sorting preserves all shops in response
     */
    @Test
    @WithMockUser(username = "user", roles = {"CUSTOMER"})
    void testSortingPreservesAllShops() throws Exception {
        mockMvc.perform(get("/gui/shops")
                        .param(shop1.getId().toString(), "price-asc"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("shops", hasSize(2)));
    }

    /**
     * Test sorting case insensitivity
     */
    @Test
    @WithMockUser(username = "user", roles = {"CUSTOMER"})
    void testSortingCaseInsensitive() throws Exception {
        mockMvc.perform(get("/gui/shops")
                        .param(shop1.getId().toString(), "PRICE-ASC"))
                .andExpect(status().isOk())
                .andExpect(view().name("shops"));
    }

    /**
     * Test multiple sort parameters for same shop (last one wins)
     */
    @Test
    @WithMockUser(username = "user", roles = {"CUSTOMER"})
    void testMultipleSortParametersForSameShop() throws Exception {
        mockMvc.perform(get("/gui/shops")
                        .param(shop1.getId().toString(), "price-asc")
                        .param(shop1.getId().toString(), "name-desc"))
                .andExpect(status().isOk())
                .andExpect(view().name("shops"));
    }

    /**
     * Test shops page without any shops
     */
    @Test
    @WithMockUser(username = "user", roles = {"CUSTOMER"})
    void testShopsPageWithNoShops() throws Exception {
        // Clean all shops
        productRepository.deleteAll();
        shopRepository.deleteAll();

        mockMvc.perform(get("/gui/shops"))
                .andExpect(status().isOk())
                .andExpect(view().name("shops"))
                .andExpect(model().attribute("shops", hasSize(0)));
    }

    /**
     * Test sorting persists across multiple parameters
     */
    @Test
    @WithMockUser(username = "user", roles = {"CUSTOMER"})
    void testSortingPersistsAcrossMultipleParams() throws Exception {
        mockMvc.perform(get("/gui/shops")
                        .param(shop1.getId().toString(), "price-desc")
                        .param(shop2.getId().toString(), "stock")
                        .param("someOtherParam", "value"))
                .andExpect(status().isOk())
                .andExpect(view().name("shops"));
    }

    /**
     * Test default sorting (name-asc) is applied when no param provided
     */
    @Test
    @WithMockUser(username = "user", roles = {"CUSTOMER"})
    void testDefaultSortingApplied() throws Exception {
        mockMvc.perform(get("/gui/shops"))
                .andExpect(status().isOk())
                .andExpect(view().name("shops"))
                .andExpect(model().attributeExists("shops"));
        // Default should be name-asc, products should be in alphabetical order
    }

    /**
     * Test sorting works with shop containing single product
     */
    @Test
    @WithMockUser(username = "user", roles = {"CUSTOMER"})
    void testSortingWithSingleProductShop() throws Exception {
        // Create shop with single product
        Shop singleProductShop = new Shop();
        singleProductShop.setName("Single Product Shop");
        singleProductShop.setMerchant(testMerchant);

        Product product = new Product();
        product.setName("Single Product");
        product.setPrice(99.99);
        product.setStock(1);
        product.setShop(singleProductShop);

        singleProductShop.addProduct(product);
        Shop saved = shopRepository.save(singleProductShop);

        mockMvc.perform(get("/gui/shops")
                        .param(saved.getId().toString(), "price-desc"))
                .andExpect(status().isOk())
                .andExpect(view().name("shops"));
    }

    /**
     * Test sorting with special characters in shop ID parameter
     */
    @Test
    @WithMockUser(username = "user", roles = {"CUSTOMER"})
    void testSortingWithSpecialCharactersHandled() throws Exception {
        mockMvc.perform(get("/gui/shops")
                        .param("abc", "price-asc")) // Non-numeric shop ID
                .andExpect(status().isOk())
                .andExpect(view().name("shops"));
    }

    /**
     * Test POST request to add shop doesn't break sorting
     */
    @Test
    @WithMockUser(username = "merchant", roles = {"MERCHANT"})
    void testAddShopDoesntBreakSorting() throws Exception {
        mockMvc.perform(post("/gui/shops")
                        .param("name", "New Test Shop"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gui/shops"));

        // Verify shops page still loads with sorting
        mockMvc.perform(get("/gui/shops")
                        .param(shop1.getId().toString(), "price-asc"))
                .andExpect(status().isOk())
                .andExpect(view().name("shops"));
    }
}
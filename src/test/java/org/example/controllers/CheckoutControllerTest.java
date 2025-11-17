package org.example.controllers;

import org.example.ShopAppApplication;
import org.example.models.*;
import org.example.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
 * Integration tests for CheckoutController
 * Tests checkout flow, validation, and order processing
 */
@SpringBootTest(classes = ShopAppApplication.class)
@AutoConfigureMockMvc
class CheckoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    private Customer testCustomer;
    private Product testProduct1;
    private Product testProduct2;
    private Shop testShop;
    private Merchant testMerchant;

    @BeforeEach
    void setUp() {
        // Clean up database
        cartRepository.deleteAll();
        productRepository.deleteAll();
        shopRepository.deleteAll();
        merchantRepository.deleteAll();
        customerRepository.deleteAll();

        // Create test merchant
        testMerchant = new Merchant();
        testMerchant.setName("Test Merchant");
        testMerchant.setEmail("merchant@test.com");
        testMerchant.setPassword("password");
        merchantRepository.save(testMerchant);

        // Create test shop
        testShop = new Shop();
        testShop.setName("Test Shop");
        testShop.setMerchant(testMerchant);
        shopRepository.save(testShop);

        // Create test products
        testProduct1 = new Product();
        testProduct1.setName("Test Product 1");
        testProduct1.setDescription("Description 1");
        testProduct1.setPrice(25.99);
        testProduct1.setStock(100);
        testProduct1.setShop(testShop);
        productRepository.save(testProduct1);

        testProduct2 = new Product();
        testProduct2.setName("Test Product 2");
        testProduct2.setDescription("Description 2");
        testProduct2.setPrice(15.50);
        testProduct2.setStock(50);
        testProduct2.setShop(testShop);
        productRepository.save(testProduct2);

        // Create test customer with cart

        testCustomer = new Customer();
        testCustomer.setName("Test Customer");
        testCustomer.setEmail("customer@test.com");
        testCustomer.setPassword("password");
        Cart cart = new Cart(testCustomer);
        testCustomer.setCart(cart);

        customerRepository.save(testCustomer);
    }

    @AfterEach
    void cleanup() {
        customerRepository.deleteAll();
        productRepository.deleteAll();
        shopRepository.deleteAll();
        merchantRepository.deleteAll();
    }

    /**
     * Test accessing checkout page without authentication
     */
    @Test
    void testCheckoutPageWithoutAuth() throws Exception {
        mockMvc.perform(get("/gui/customer/checkout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/gui/login"));
    }

    /**
     * Test accessing checkout page with empty cart
     */
    @Test
    @WithMockUser(username = "customer@test.com", roles = {"CUSTOMER"})
    void testCheckoutPageWithEmptyCart() throws Exception {
        mockMvc.perform(get("/gui/customer/checkout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gui/customer/cart"))
                .andExpect(flash().attributeExists("error"))
                .andExpect(flash().attribute("error",
                        containsString("Your cart is empty")));
    }

    /**
     * Test accessing checkout page with items in cart
     */
    @Test
    @WithMockUser(username = "customer@test.com", roles = {"CUSTOMER"})
    void testCheckoutPageWithCartItems() throws Exception {
        // Add items to cart
        Cart cart = testCustomer.getCart();
        cart.addProduct(testProduct1, 2);
        cart.addProduct(testProduct2, 3);
        cartRepository.save(cart);
        productRepository.save(testProduct1);
        productRepository.save(testProduct2);

        mockMvc.perform(get("/gui/customer/checkout"))
                .andExpect(status().isOk())
                .andExpect(view().name("checkout"))
                .andExpect(model().attributeExists("customer"))
                .andExpect(model().attributeExists("cart"))
                .andExpect(model().attributeExists("cartItems"))
                .andExpect(model().attributeExists("subtotal"))
                .andExpect(model().attributeExists("tax"))
                .andExpect(model().attributeExists("grandTotal"))
                .andExpect(model().attribute("subtotal", closeTo(98.48, 0.01))) // (25.99*2 + 15.50*3)
                .andExpect(model().attribute("tax", closeTo(12.80, 0.01))) // 13% tax
                .andExpect(model().attribute("grandTotal", closeTo(111.28, 0.01)));
    }

    /**
     * Test successful checkout process
     */
    @Test
    @WithMockUser(username = "customer@test.com", roles = {"CUSTOMER"})
    void testSuccessfulCheckout() throws Exception {
        // Add items to cart
        Cart cart = testCustomer.getCart();
        cart.addProduct(testProduct1, 2);
        cartRepository.save(cart);
        productRepository.save(testProduct1);

        // Initial stock check
        int initialStock = testProduct1.getStock();

        mockMvc.perform(post("/gui/customer/checkout/process")
                        .param("fullName", "John Doe")
                        .param("email", "john@test.com")
                        .param("address", "123 Main St")
                        .param("city", "Ottawa")
                        .param("province", "ON")
                        .param("postalCode", "K1A 0B1")
                        .param("cardNumber", "1234567890123456")
                        .param("cardName", "John Doe")
                        .param("expiryDate", "12/25")
                        .param("cvv", "123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gui/customer/checkout/confirmation"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(flash().attributeExists("orderTotal"))
                .andExpect(flash().attributeExists("orderEmail"));

        // Verify cart was cleared
        Cart updatedCart = cartRepository.findById(cart.getCartID()).orElseThrow();
        assertEquals(0, updatedCart.getCartItems().size());

        // Note: Stock should have been reduced when items were added to cart
        // So we just verify the cart is now empty
    }

    /**
     * Test checkout with missing required fields
     */
    @Test
    @WithMockUser(username = "customer@test.com", roles = {"CUSTOMER"})
    void testCheckoutWithMissingFields() throws Exception {
        // Add items to cart
        Cart cart = testCustomer.getCart();
        cart.addProduct(testProduct1, 1);
        cartRepository.save(cart);
        productRepository.save(testProduct1);

        mockMvc.perform(post("/gui/customer/checkout/process")
                        .param("fullName", "John Doe")
                        .param("email", "") // Missing email
                        .param("address", "123 Main St")
                        .param("city", "Ottawa")
                        .param("province", "ON")
                        .param("postalCode", "K1A 0B1")
                        .param("cardNumber", "1234567890123456")
                        .param("cardName", "John Doe")
                        .param("expiryDate", "12/25")
                        .param("cvv", "123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gui/customer/checkout"))
                .andExpect(flash().attributeExists("error"))
                .andExpect(flash().attribute("error",
                        containsString("Please fill in all required fields")));
    }

    /**
     * Test checkout with invalid card number
     */
    @Test
    @WithMockUser(username = "customer@test.com", roles = {"CUSTOMER"})
    void testCheckoutWithInvalidCardNumber() throws Exception {
        // Add items to cart
        Cart cart = testCustomer.getCart();
        cart.addProduct(testProduct1, 1);
        cartRepository.save(cart);
        productRepository.save(testProduct1);

        mockMvc.perform(post("/gui/customer/checkout/process")
                        .param("fullName", "John Doe")
                        .param("email", "john@test.com")
                        .param("address", "123 Main St")
                        .param("city", "Ottawa")
                        .param("province", "ON")
                        .param("postalCode", "K1A 0B1")
                        .param("cardNumber", "12345") // Invalid - too short
                        .param("cardName", "John Doe")
                        .param("expiryDate", "12/25")
                        .param("cvv", "123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gui/customer/checkout"))
                .andExpect(flash().attributeExists("error"))
                .andExpect(flash().attribute("error",
                        containsString("Invalid card number")));
    }

    /**
     * Test checkout with invalid CVV
     */
    @Test
    @WithMockUser(username = "customer@test.com", roles = {"CUSTOMER"})
    void testCheckoutWithInvalidCVV() throws Exception {
        // Add items to cart
        Cart cart = testCustomer.getCart();
        cart.addProduct(testProduct1, 1);
        cartRepository.save(cart);
        productRepository.save(testProduct1);

        mockMvc.perform(post("/gui/customer/checkout/process")
                        .param("fullName", "John Doe")
                        .param("email", "john@test.com")
                        .param("address", "123 Main St")
                        .param("city", "Ottawa")
                        .param("province", "ON")
                        .param("postalCode", "K1A 0B1")
                        .param("cardNumber", "1234567890123456")
                        .param("cardName", "John Doe")
                        .param("expiryDate", "12/25")
                        .param("cvv", "12")) // Invalid - too short
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gui/customer/checkout"))
                .andExpect(flash().attributeExists("error"))
                .andExpect(flash().attribute("error",
                        containsString("Invalid CVV")));
    }

    /**
     * Test checkout with invalid expiry date format
     */
    @Test
    @WithMockUser(username = "customer@test.com", roles = {"CUSTOMER"})
    void testCheckoutWithInvalidExpiryDate() throws Exception {
        // Add items to cart
        Cart cart = testCustomer.getCart();
        cart.addProduct(testProduct1, 1);
        cartRepository.save(cart);
        productRepository.save(testProduct1);

        mockMvc.perform(post("/gui/customer/checkout/process")
                        .param("fullName", "John Doe")
                        .param("email", "john@test.com")
                        .param("address", "123 Main St")
                        .param("city", "Ottawa")
                        .param("province", "ON")
                        .param("postalCode", "K1A 0B1")
                        .param("cardNumber", "1234567890123456")
                        .param("cardName", "John Doe")
                        .param("expiryDate", "13/25") // Invalid - month > 12
                        .param("cvv", "123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gui/customer/checkout"))
                .andExpect(flash().attributeExists("error"))
                .andExpect(flash().attribute("error",
                        containsString("Invalid expiry date")));
    }

    /**
     * Test checkout with card number containing spaces (should be accepted)
     */
    @Test
    @WithMockUser(username = "customer@test.com", roles = {"CUSTOMER"})
    void testCheckoutWithCardNumberSpaces() throws Exception {
        // Add items to cart
        Cart cart = testCustomer.getCart();
        cart.addProduct(testProduct1, 1);
        cartRepository.save(cart);
        productRepository.save(testProduct1);

        mockMvc.perform(post("/gui/customer/checkout/process")
                        .param("fullName", "John Doe")
                        .param("email", "john@test.com")
                        .param("address", "123 Main St")
                        .param("city", "Ottawa")
                        .param("province", "ON")
                        .param("postalCode", "K1A 0B1")
                        .param("cardNumber", "1234 5678 9012 3456") // With spaces
                        .param("cardName", "John Doe")
                        .param("expiryDate", "12/25")
                        .param("cvv", "123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gui/customer/checkout/confirmation"));
    }

    /**
     * Test checkout with 4-digit CVV (Amex)
     */
    @Test
    @WithMockUser(username = "customer@test.com", roles = {"CUSTOMER"})
    void testCheckoutWithFourDigitCVV() throws Exception {
        // Add items to cart
        Cart cart = testCustomer.getCart();
        cart.addProduct(testProduct1, 1);
        cartRepository.save(cart);
        productRepository.save(testProduct1);

        mockMvc.perform(post("/gui/customer/checkout/process")
                        .param("fullName", "John Doe")
                        .param("email", "john@test.com")
                        .param("address", "123 Main St")
                        .param("city", "Ottawa")
                        .param("province", "ON")
                        .param("postalCode", "K1A 0B1")
                        .param("cardNumber", "1234567890123456")
                        .param("cardName", "John Doe")
                        .param("expiryDate", "12/25")
                        .param("cvv", "1234")) // 4-digit CVV
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gui/customer/checkout/confirmation"));
    }

    /**
     * Test checkout process without authentication
     */
    @Test
    void testCheckoutProcessWithoutAuth() throws Exception {
        mockMvc.perform(post("/gui/customer/checkout/process")
                        .param("fullName", "John Doe")
                        .param("email", "john@test.com")
                        .param("address", "123 Main St")
                        .param("city", "Ottawa")
                        .param("province", "ON")
                        .param("postalCode", "K1A 0B1")
                        .param("cardNumber", "1234567890123456")
                        .param("cardName", "John Doe")
                        .param("expiryDate", "12/25")
                        .param("cvv", "123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/gui/login"));
    }

    /**
     * Test checkout with empty cart
     */
    @Test
    @WithMockUser(username = "customer@test.com", roles = {"CUSTOMER"})
    void testCheckoutProcessWithEmptyCart() throws Exception {
        mockMvc.perform(post("/gui/customer/checkout/process")
                        .param("fullName", "John Doe")
                        .param("email", "john@test.com")
                        .param("address", "123 Main St")
                        .param("city", "Ottawa")
                        .param("province", "ON")
                        .param("postalCode", "K1A 0B1")
                        .param("cardNumber", "1234567890123456")
                        .param("cardName", "John Doe")
                        .param("expiryDate", "12/25")
                        .param("cvv", "123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gui/customer/cart"))
                .andExpect(flash().attributeExists("error"))
                .andExpect(flash().attribute("error", containsString("Your cart is empty")));
    }

    /**
     * Test confirmation page without order data
     */
    @Test
    @WithMockUser(username = "customer@test.com", roles = {"CUSTOMER"})
    void testConfirmationPageWithoutOrderData() throws Exception {
        mockMvc.perform(get("/gui/customer/checkout/confirmation"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gui/shops"));
    }

    /**
     * Test confirmation page without authentication
     */
    @Test
    void testConfirmationPageWithoutAuth() throws Exception {
        mockMvc.perform(get("/gui/customer/checkout/confirmation"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/gui/login"));
    }

    /**
     * Test complete checkout flow
     */
    @Test
    @WithMockUser(username = "customer@test.com", roles = {"CUSTOMER"})
    void testCompleteCheckoutFlow() throws Exception {
        // Step 1: Add items to cart
        Cart cart = testCustomer.getCart();
        cart.addProduct(testProduct1, 2);
        cart.addProduct(testProduct2, 1);
        cartRepository.save(cart);
        productRepository.save(testProduct1);
        productRepository.save(testProduct2);

        // Step 2: View checkout page
        mockMvc.perform(get("/gui/customer/checkout"))
                .andExpect(status().isOk())
                .andExpect(view().name("checkout"));

        // Step 3: Process checkout
        mockMvc.perform(post("/gui/customer/checkout/process")
                        .param("fullName", "Jane Smith")
                        .param("email", "jane@test.com")
                        .param("address", "456 Oak Ave")
                        .param("city", "Toronto")
                        .param("province", "ON")
                        .param("postalCode", "M5H 2N2")
                        .param("cardNumber", "9876543210123456")
                        .param("cardName", "Jane Smith")
                        .param("expiryDate", "06/26")
                        .param("cvv", "456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gui/customer/checkout/confirmation"));

        // Step 4: Verify cart is cleared
        Cart updatedCart = cartRepository.findById(cart.getCartID()).orElseThrow();
        assertEquals(0, updatedCart.getCartItems().size());
    }

    /**
     * Test checkout calculates correct totals
     */
    @Test
    @WithMockUser(username = "customer@test.com", roles = {"CUSTOMER"})
    void testCheckoutCalculatesTotals() throws Exception {
        // Add specific items to test calculation
        Cart cart = testCustomer.getCart();
        cart.addProduct(testProduct1, 3); // 3 * 25.99 = 77.97
        cart.addProduct(testProduct2, 2); // 2 * 15.50 = 31.00
        cartRepository.save(cart);
        productRepository.save(testProduct1);
        productRepository.save(testProduct2);

        // Total: 77.97 + 31.00 = 108.97
        // Tax (13%): 108.97 * 0.13 = 14.17
        // Grand Total: 108.97 + 14.17 = 123.14

        mockMvc.perform(get("/gui/customer/checkout"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("subtotal", closeTo(108.97, 0.01)))
                .andExpect(model().attribute("tax", closeTo(14.17, 0.01)))
                .andExpect(model().attribute("grandTotal", closeTo(123.14, 0.01)));
    }

    /**
     * Test merchant cannot access checkout
     */
    @Test
    @WithMockUser(username = "merchant@test.com", roles = {"MERCHANT"})
    void testMerchantCannotAccessCheckout() throws Exception {
        mockMvc.perform(get("/gui/customer/checkout"))
                .andExpect(status().isForbidden());
    }
}
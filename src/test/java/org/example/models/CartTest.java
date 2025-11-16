package org.example.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CartTest {

    private Cart cart;
    private Customer customer;
    private Product product1;
    private Product product2;
    private Shop shop;
    private Merchant merchant;

    @BeforeEach
    void setUp() {
        // Create merchant
        merchant = new Merchant();
        merchant.setId(1L);
        merchant.setName("Test Merchant");
        merchant.setEmail("merchant@test.com");

        // Create shop
        shop = new Shop();
        shop.setId(1L);
        shop.setName("Test Shop");
        shop.setMerchant(merchant);

        // Create products
        product1 = new Product();
        product1.setId(1L);
        product1.setName("Product 1");
        product1.setPrice(10.0);
        product1.setStock(100);
        product1.setShop(shop);

        product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setPrice(20.0);
        product2.setStock(50);
        product2.setShop(shop);

        // Create customer and cart
        customer = new Customer("customer@test.com", "password123", "Test Customer");
        customer.setId(1L);
        cart = new Cart(customer);
    }

    @Test
    void testCartConstructorWithCustomer() {
        Cart newCart = new Cart(customer);

        assertNotNull(newCart);
        assertEquals(customer, newCart.getCustomer());
        assertNotNull(newCart.getCartItems());
        assertTrue(newCart.getCartItems().isEmpty());
    }

    @Test
    void testCartDefaultConstructor() {
        Cart newCart = new Cart();

        assertNotNull(newCart);
        assertNotNull(newCart.getCartItems());
        assertTrue(newCart.getCartItems().isEmpty());
    }

    @Test
    void testGetCartID() {
        cart.setCartID(15L);
        assertEquals(15L, cart.getCartID());
    }

    @Test
    void testSetCartID() {
        cart.setCartID(999L);
        assertEquals(999L, cart.getCartID());
    }

    @Test
    void testGetCustomer() {
        assertEquals(customer, cart.getCustomer());
    }

    @Test
    void testSetCustomer() {
        Customer newCustomer = new Customer("new@test.com", "pass", "New Customer");
        cart.setCustomer(newCustomer);
        assertEquals(newCustomer, cart.getCustomer());
    }

    @Test
    void testGetCartItems() {
        assertNotNull(cart.getCartItems());
        assertTrue(cart.getCartItems().isEmpty());

        // Add an item and test again
        cart.addProduct(product1, 5);
        assertEquals(1, cart.getCartItems().size());
    }

    @Test
    void testSetCartItems() {
        CartItem item1 = new CartItem(cart, product1, 3);
        CartItem item2 = new CartItem(cart, product2, 2);

        java.util.List<CartItem> items = new java.util.ArrayList<>();
        items.add(item1);
        items.add(item2);

        cart.setCartItems(items);

        assertEquals(2, cart.getCartItems().size());
        assertTrue(cart.getCartItems().contains(item1));
        assertTrue(cart.getCartItems().contains(item2));
    }

    @Test
    void testAddProductNewProduct() {
        int initialStock = product1.getStock();
        int quantityToAdd = 5;

        boolean result = cart.addProduct(product1, quantityToAdd);

        assertTrue(result);
        assertEquals(1, cart.getCartItems().size());
        assertEquals(initialStock - quantityToAdd, product1.getStock());

        CartItem addedItem = cart.getCartItems().get(0);
        assertEquals(product1, addedItem.getProduct());
        assertEquals(quantityToAdd, addedItem.getQuantity());
    }

    @Test
    void testAddProductExistingProduct() {
        // Add product first time
        cart.addProduct(product1, 5);
        int stockAfterFirstAdd = product1.getStock();

        // Add same product again
        boolean result = cart.addProduct(product1, 3);

        assertTrue(result);
        assertEquals(1, cart.getCartItems().size()); // Still only one cart item
        assertEquals(8, cart.getCartItems().get(0).getQuantity()); // Quantity increased
        assertEquals(stockAfterFirstAdd - 3, product1.getStock());
    }

    @Test
    void testAddProductInsufficientStock() {
        int initialStock = product1.getStock();
        int quantityToAdd = initialStock + 10; // More than available

        boolean result = cart.addProduct(product1, quantityToAdd);

        assertFalse(result);
        assertEquals(0, cart.getCartItems().size());
        assertEquals(initialStock, product1.getStock()); // Stock unchanged
    }

    @Test
    void testAddProductExactStock() {
        int initialStock = product1.getStock();

        boolean result = cart.addProduct(product1, initialStock);

        assertTrue(result);
        assertEquals(1, cart.getCartItems().size());
        assertEquals(0, product1.getStock());
        assertEquals(initialStock, cart.getCartItems().get(0).getQuantity());
    }

    @Test
    void testRemoveProductExists() {
        // Add product first
        cart.addProduct(product1, 5);
        int stockAfterAdd = product1.getStock();

        // Remove product
        boolean result = cart.removeProduct(product1);

        assertTrue(result);
        assertEquals(0, cart.getCartItems().size());
        assertEquals(stockAfterAdd + 5, product1.getStock()); // Stock returned
    }

    @Test
    void testRemoveProductNotInCart() {
        int initialStock = product1.getStock();

        boolean result = cart.removeProduct(product1);

        assertFalse(result);
        assertEquals(0, cart.getCartItems().size());
        assertEquals(initialStock, product1.getStock());
    }

    @Test
    void testRemoveProductMultipleItems() {
        // Add two different products
        cart.addProduct(product1, 5);
        cart.addProduct(product2, 3);

        // Remove one product
        boolean result = cart.removeProduct(product1);

        assertTrue(result);
        assertEquals(1, cart.getCartItems().size());
        assertEquals(product2, cart.getCartItems().get(0).getProduct());
    }

    @Test
    void testReduceProductQuantityPartial() {
        // Add product
        cart.addProduct(product1, 10);
        int stockAfterAdd = product1.getStock();

        // Reduce quantity by 4
        boolean result = cart.reduceProductQuantity(product1, 4);

        assertTrue(result);
        assertEquals(1, cart.getCartItems().size());
        assertEquals(6, cart.getCartItems().get(0).getQuantity());
        assertEquals(stockAfterAdd + 4, product1.getStock());
    }

    @Test
    void testReduceProductQuantityToZero() {
        // Add product
        cart.addProduct(product1, 10);
        int stockAfterAdd = product1.getStock();

        // Reduce quantity to 0
        boolean result = cart.reduceProductQuantity(product1, 10);

        assertTrue(result);
        assertEquals(0, cart.getCartItems().size()); // Item removed
        assertEquals(stockAfterAdd + 10, product1.getStock());
    }

    @Test
    void testReduceProductQuantityMoreThanAvailable() {
        // Add product
        cart.addProduct(product1, 5);
        int stockAfterAdd = product1.getStock();

        // Try to reduce by more than quantity
        boolean result = cart.reduceProductQuantity(product1, 10);

        assertFalse(result);
        assertEquals(1, cart.getCartItems().size());
        assertEquals(5, cart.getCartItems().get(0).getQuantity());
        assertEquals(stockAfterAdd, product1.getStock()); // Stock unchanged
    }

    @Test
    void testReduceProductQuantityNotInCart() {
        int initialStock = product1.getStock();

        boolean result = cart.reduceProductQuantity(product1, 5);

        assertFalse(result);
        assertEquals(0, cart.getCartItems().size());
        assertEquals(initialStock, product1.getStock());
    }

    @Test
    void testCalculateTotalEmpty() {
        double total = cart.calculateTotal();
        assertEquals(0.0, total, 0.01);
    }

    @Test
    void testCalculateTotalSingleItem() {
        cart.addProduct(product1, 5);

        double total = cart.calculateTotal();
        assertEquals(50.0, total, 0.01); // 5 * 10.0
    }

    @Test
    void testCalculateTotalMultipleItems() {
        cart.addProduct(product1, 5); // 5 * 10.0 = 50.0
        cart.addProduct(product2, 3); // 3 * 20.0 = 60.0

        double total = cart.calculateTotal();
        assertEquals(110.0, total, 0.01);
    }

    @Test
    void testCalculateTotalAfterQuantityChange() {
        cart.addProduct(product1, 10);
        cart.reduceProductQuantity(product1, 3);

        double total = cart.calculateTotal();
        assertEquals(70.0, total, 0.01); // 7 * 10.0
    }

    @Test
    void testClearCartEmpty() {
        cart.clearCart();

        assertEquals(0, cart.getCartItems().size());
    }

    @Test
    void testClearCartWithItems() {
        // Add multiple products
        cart.addProduct(product1, 5);
        int product1StockAfterAdd = product1.getStock();
        cart.addProduct(product2, 3);
        int product2StockAfterAdd = product2.getStock();

        // Clear cart
        cart.clearCart();

        assertEquals(0, cart.getCartItems().size());
        assertEquals(product1StockAfterAdd + 5, product1.getStock());
        assertEquals(product2StockAfterAdd + 3, product2.getStock());
    }

    @Test
    void testAddProductMultipleTimesSameProduct() {
        // Verify that adding the same product multiple times always merges
        cart.addProduct(product1, 2);
        cart.addProduct(product1, 3);
        cart.addProduct(product1, 5);

        // Should still be only ONE CartItem with merged quantity
        assertEquals(1, cart.getCartItems().size());
        assertEquals(10, cart.getCartItems().get(0).getQuantity()); // 2 + 3 + 5 = 10
        assertEquals(product1, cart.getCartItems().get(0).getProduct());
    }

    @Test
    void testClearCartMultipleTimes() {
        cart.addProduct(product1, 5);
        cart.clearCart();

        // Clear again should not cause issues
        cart.clearCart();

        assertEquals(0, cart.getCartItems().size());
    }
}
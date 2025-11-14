package org.example.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CartItemTest {

    private CartItem cartItem;
    private Cart cart;
    private Customer customer;
    private Product product;
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

        // Create product
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(15.99);
        product.setStock(100);
        product.setDescription("A test product");
        product.setShop(shop);

        // Create customer and cart
        customer = new Customer("test@test.com", "password123", "Test User");
        customer.setId(1L);
        cart = new Cart(customer);
        cart.setCartID(1L);

        // Create cart item
        cartItem = new CartItem(cart, product, 5);
    }

    @Test
    void testCartItemDefaultConstructor() {
        CartItem newCartItem = new CartItem();

        assertNotNull(newCartItem);
        assertNull(newCartItem.getId());
        assertNull(newCartItem.getCart());
        assertNull(newCartItem.getProduct());
        assertEquals(0, newCartItem.getQuantity());
    }

    @Test
    void testCartItemParameterizedConstructor() {
        CartItem newCartItem = new CartItem(cart, product, 10);

        assertNotNull(newCartItem);
        assertEquals(cart, newCartItem.getCart());
        assertEquals(product, newCartItem.getProduct());
        assertEquals(10, newCartItem.getQuantity());
    }

    @Test
    void testGetId() {
        cartItem.setId(100L);
        assertEquals(100L, cartItem.getId());
    }

    @Test
    void testSetId() {
        cartItem.setId(999L);
        assertEquals(999L, cartItem.getId());
    }

    @Test
    void testGetCart() {
        assertEquals(cart, cartItem.getCart());
    }

    @Test
    void testSetCart() {
        Customer newCustomer = new Customer("new@test.com", "pass", "New User");
        newCustomer.setId(2L);
        Cart newCart = new Cart(newCustomer);
        newCart.setCartID(2L);

        cartItem.setCart(newCart);

        assertEquals(newCart, cartItem.getCart());
    }

    @Test
    void testGetProduct() {
        assertEquals(product, cartItem.getProduct());
        assertEquals("Test Product", cartItem.getProduct().getName());
        assertEquals(15.99, cartItem.getProduct().getPrice(), 0.01);
    }

    @Test
    void testSetProduct() {
        Product newProduct = new Product();
        newProduct.setId(2L);
        newProduct.setName("New Product");
        newProduct.setPrice(25.50);
        newProduct.setStock(50);
        newProduct.setShop(shop);

        cartItem.setProduct(newProduct);

        assertEquals(newProduct, cartItem.getProduct());
        assertEquals("New Product", cartItem.getProduct().getName());
    }

    @Test
    void testGetQuantity() {
        assertEquals(5, cartItem.getQuantity());
    }

    @Test
    void testSetQuantity() {
        cartItem.setQuantity(15);
        assertEquals(15, cartItem.getQuantity());
    }

    @Test
    void testSetQuantityZero() {
        cartItem.setQuantity(0);
        assertEquals(0, cartItem.getQuantity());
    }

    @Test
    void testSetQuantityNegative() {
        // Note: In production, you might want to add validation
        // This test documents current behavior
        cartItem.setQuantity(-5);
        assertEquals(-5, cartItem.getQuantity());
    }

    @Test
    void testCartItemWithDifferentProducts() {
        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setPrice(10.0);
        product2.setStock(75);
        product2.setShop(shop);

        CartItem item1 = new CartItem(cart, product, 3);
        CartItem item2 = new CartItem(cart, product2, 7);

        assertNotEquals(item1.getProduct(), item2.getProduct());
        assertEquals(3, item1.getQuantity());
        assertEquals(7, item2.getQuantity());
    }

    @Test
    void testCartItemIndependence() {
        CartItem item1 = new CartItem(cart, product, 5);
        CartItem item2 = new CartItem(cart, product, 10);

        // Both items reference same product but have different quantities
        assertEquals(item1.getProduct(), item2.getProduct());
        assertNotEquals(item1.getQuantity(), item2.getQuantity());

        // Changing one doesn't affect the other
        item1.setQuantity(20);
        assertEquals(20, item1.getQuantity());
        assertEquals(10, item2.getQuantity());
    }

    @Test
    void testCartItemWithNullCart() {
        CartItem item = new CartItem();
        item.setCart(null);
        item.setProduct(product);
        item.setQuantity(5);

        assertNull(item.getCart());
        assertEquals(product, item.getProduct());
        assertEquals(5, item.getQuantity());
    }

    @Test
    void testCartItemWithNullProduct() {
        CartItem item = new CartItem();
        item.setCart(cart);
        item.setProduct(null);
        item.setQuantity(5);

        assertEquals(cart, item.getCart());
        assertNull(item.getProduct());
        assertEquals(5, item.getQuantity());
    }

    @Test
    void testMultipleCartItemsInSameCart() {
        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setPrice(20.0);
        product2.setStock(50);
        product2.setShop(shop);

        CartItem item1 = new CartItem(cart, product, 3);
        CartItem item2 = new CartItem(cart, product2, 5);

        assertEquals(cart, item1.getCart());
        assertEquals(cart, item2.getCart());
        assertNotEquals(item1.getProduct(), item2.getProduct());
    }

    @Test
    void testCartItemQuantityUpdate() {
        assertEquals(5, cartItem.getQuantity());

        cartItem.setQuantity(cartItem.getQuantity() + 3);
        assertEquals(8, cartItem.getQuantity());

        cartItem.setQuantity(cartItem.getQuantity() - 2);
        assertEquals(6, cartItem.getQuantity());
    }

    @Test
    void testCartItemProductPriceCalculation() {
        int quantity = cartItem.getQuantity();
        double price = cartItem.getProduct().getPrice();
        double expectedTotal = quantity * price;

        assertEquals(79.95, expectedTotal, 0.01); // 5 * 15.99
    }
}
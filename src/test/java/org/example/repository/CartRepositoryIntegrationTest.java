package org.example.repository;

import org.example.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class CartRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CartRepository cartRepository;

    private Customer customer;
    private Cart cart;
    private Product product1;
    private Product product2;
    private Shop shop;

    @BeforeEach
    public void setup() {
        // Create a merchant
        Merchant merchant = new Merchant();
        merchant.setEmail("merchant@test.com");
        merchant.setPassword("password");
        merchant.setName("Test Merchant");
        entityManager.persist(merchant);

        // Create a shop
        shop = new Shop();
        shop.setName("Test Shop");
        shop.setMerchant(merchant);
        entityManager.persist(shop);

        // Create products
        product1 = new Product();
        product1.setName("Product 1");
        product1.setDescription("Description 1");
        product1.setPrice(10.99);
        product1.setStock(100);
        product1.setShop(shop);
        entityManager.persist(product1);

        product2 = new Product();
        product2.setName("Product 2");
        product2.setDescription("Description 2");
        product2.setPrice(20.99);
        product2.setStock(50);
        product2.setShop(shop);
        entityManager.persist(product2);

        // Create a customer
        customer = new Customer();
        customer.setEmail("customer@test.com");
        customer.setPassword("password");
        customer.setName("Test Customer");
        entityManager.persist(customer);

        // Create a cart
        cart = new Cart(customer);
        customer.setCart(cart);
        entityManager.persist(cart);

        entityManager.flush();
    }

    @Test
    public void testSaveCart() {
        Cart savedCart = cartRepository.save(cart);

        assertNotNull(savedCart);
        assertNotNull(savedCart.getCartID());
        assertEquals(customer.getId(), savedCart.getCustomer().getId());
    }

    @Test
    public void testFindCartById() {
        Cart savedCart = cartRepository.save(cart);

        Optional<Cart> foundCart = cartRepository.findById(savedCart.getCartID());

        assertTrue(foundCart.isPresent());
        assertEquals(savedCart.getCartID(), foundCart.get().getCartID());
        assertEquals(customer.getEmail(), foundCart.get().getCustomer().getEmail());
    }

    @Test
    public void testSaveCartWithItems() {
        // Add items to cart
        cart.addProduct(product1, 2);
        cart.addProduct(product2, 3);

        Cart savedCart = cartRepository.save(cart);
        entityManager.flush();
        entityManager.clear();

        Optional<Cart> foundCart = cartRepository.findById(savedCart.getCartID());

        assertTrue(foundCart.isPresent());
        assertEquals(2, foundCart.get().getCartItems().size());
    }

    @Test
    public void testUpdateCart() {
        cart.addProduct(product1, 1);
        Cart savedCart = cartRepository.save(cart);
        entityManager.flush();

        // Add another product
        savedCart.addProduct(product2, 2);
        Cart updatedCart = cartRepository.save(savedCart);
        entityManager.flush();
        entityManager.clear();

        Optional<Cart> foundCart = cartRepository.findById(updatedCart.getCartID());

        assertTrue(foundCart.isPresent());
        assertEquals(2, foundCart.get().getCartItems().size());
    }

    @Test
    public void testDeleteCart() {
        Cart savedCart = cartRepository.save(cart);
        Long cartId = savedCart.getCartID();

        // Remove the relationship from customer first
        customer.setCart(null);
        entityManager.persist(customer);
        entityManager.flush();

        cartRepository.delete(savedCart);
        entityManager.flush();

        Optional<Cart> foundCart = cartRepository.findById(cartId);

        assertFalse(foundCart.isPresent());
    }

    @Test
    public void testCartItemsCascadeDelete() {
        cart.addProduct(product1, 2);
        cart.addProduct(product2, 3);
        Cart savedCart = cartRepository.save(cart);
        entityManager.flush();

        Long cartId = savedCart.getCartID();
        int itemCount = savedCart.getCartItems().size();

        assertEquals(2, itemCount);

        // Remove the relationship from customer first
        customer.setCart(null);
        entityManager.persist(customer);
        entityManager.flush();

        // Delete cart - items should cascade delete
        cartRepository.delete(savedCart);
        entityManager.flush();
        entityManager.clear();

        Optional<Cart> foundCart = cartRepository.findById(cartId);
        assertFalse(foundCart.isPresent());
    }

    @Test
    public void testFindAllCarts() {
        // Create another customer and cart
        Customer customer2 = new Customer();
        customer2.setEmail("customer2@test.com");
        customer2.setPassword("password");
        customer2.setName("Test Customer 2");
        entityManager.persist(customer2);

        Cart cart2 = new Cart(customer2);
        customer2.setCart(cart2);
        entityManager.persist(cart2);

        cartRepository.save(cart);
        cartRepository.save(cart2);
        entityManager.flush();

        Iterable<Cart> carts = cartRepository.findAll();

        assertNotNull(carts);
        long count = 0;
        for (Cart c : carts) {
            count++;
        }
        assertEquals(2, count);
    }

    @Test
    public void testCartCalculateTotal() {
        cart.addProduct(product1, 2);  // 2 * 10.99 = 21.98
        cart.addProduct(product2, 1);  // 1 * 20.99 = 20.99
        Cart savedCart = cartRepository.save(cart);
        entityManager.flush();
        entityManager.clear();

        Optional<Cart> foundCart = cartRepository.findById(savedCart.getCartID());

        assertTrue(foundCart.isPresent());
        double total = foundCart.get().calculateTotal();
        assertEquals(42.97, total, 0.01);
    }

    @Test
    public void testCartClearItems() {
        cart.addProduct(product1, 2);
        cart.addProduct(product2, 3);
        Cart savedCart = cartRepository.save(cart);
        entityManager.flush();

        savedCart.clearCart();
        Cart clearedCart = cartRepository.save(savedCart);
        entityManager.flush();
        entityManager.clear();

        Optional<Cart> foundCart = cartRepository.findById(clearedCart.getCartID());

        assertTrue(foundCart.isPresent());
        assertEquals(0, foundCart.get().getCartItems().size());
    }

    @Test
    public void testCartExistsById() {
        Cart savedCart = cartRepository.save(cart);
        entityManager.flush();

        boolean exists = cartRepository.existsById(savedCart.getCartID());

        assertTrue(exists);
    }

    @Test
    public void testCartNotExistsById() {
        boolean exists = cartRepository.existsById(999L);

        assertFalse(exists);
    }

    @Test
    public void testCountCarts() {
        cartRepository.save(cart);
        entityManager.flush();

        long count = cartRepository.count();

        assertTrue(count > 0);
    }
}
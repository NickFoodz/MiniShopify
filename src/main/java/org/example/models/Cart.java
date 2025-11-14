package org.example.models;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Cart {

    @Id
    private Long cartID;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems;

    @OneToOne
    @MapsId
    @JoinColumn(name="cartID")
    private Customer customer;

    /**
     * Basic Constructor for spring
     */
    public Cart() {
        this.cartItems = new ArrayList<>();
    }

    /**
     * Constructor for Cart object
     * @param customer the customer this cart belongs to
     */
    public Cart(Customer customer) {
        this.customer = customer;
        this.cartItems = new ArrayList<>();
    }

    /**
     * Getter for cartID
     * @return cartID the ID of this cart
     */
    public Long getCartID() {
        return cartID;
    }

    /**
     * Setter for cartID (for testing)
     * @param cartID the cartID to set
     */
    public void setCartID(Long cartID) {
        this.cartID = cartID;
    }

    /**
     * Getter for the customer
     * @return customer the cart belongs to
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Setter for the customer field
     * @param customer the customer this cart will belong to
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    /**
     * Getter for list of cart items
     * @return the list of cart items
     */
    public List<CartItem> getCartItems() {
        return cartItems;
    }

    /**
     * Setter for the cart items
     * @param cartItems the list of cart items to set
     */
    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    /**
     * Adds a quantity of a product to the cart and reduces merchant's stock
     * @param product the product to add to the cart
     * @param quantity the quantity to add
     * @return true if product added successfully, false if not enough stock
     */
    public boolean addProduct(Product product, int quantity) {
        // Check if merchant has enough stock
        if (!product.removeStock(quantity)) {
            return false; // Not enough stock
        }

        // Check if product already in cart
        CartItem existingItem = findCartItemByProductId(product.getId());

        if (existingItem != null) {
            // Product already in cart, increase quantity
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            // New product, create new cart item
            CartItem newItem = new CartItem(this, product, quantity);
            cartItems.add(newItem);
        }

        return true;
    }

    /**
     * Removes a product completely from cart and returns stock to merchant
     * @param product the product to remove from the cart
     * @return true if product was removed, false if product wasn't in cart
     */
    public boolean removeProduct(Product product) {
        CartItem item = findCartItemByProductId(product.getId());

        if (item != null) {
            // Return stock to merchant
            product.addStock(item.getQuantity());
            // Remove from cart
            cartItems.remove(item);
            return true;
        }
        return false;
    }

    /**
     * Reduces quantity of a product in cart by specified amount
     * @param product the product to reduce quantity for
     * @param quantity the amount to reduce by
     * @return true if successful, false otherwise
     */
    public boolean reduceProductQuantity(Product product, int quantity) {
        CartItem item = findCartItemByProductId(product.getId());

        if (item != null && item.getQuantity() >= quantity) {
            // Return stock to merchant
            product.addStock(quantity);

            int newQuantity = item.getQuantity() - quantity;
            if (newQuantity == 0) {
                // Remove item if quantity reaches 0
                cartItems.remove(item);
            } else {
                item.setQuantity(newQuantity);
            }
            return true;
        }
        return false;
    }

    /**
     * Calculates total cost of all items in cart
     * @return the total cost
     */
    public double calculateTotal() {
        return cartItems.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
    }

    /**
     * Finds a cart item by product ID
     * @param productID product ID being searched
     * @return the CartItem if it exists, or null if it does not
     */
    private CartItem findCartItemByProductId(Long productID) {
        return cartItems.stream()
                .filter(item -> item.getProduct().getId().equals(productID))
                .findFirst()
                .orElse(null);
    }

    /**
     * Clears the cart and returns all stock to merchants
     */
    public void clearCart() {
        for (CartItem item : cartItems) {
            item.getProduct().addStock(item.getQuantity());
        }
        cartItems.clear();
    }
}
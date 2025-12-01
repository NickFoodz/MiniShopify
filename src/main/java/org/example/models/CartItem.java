package org.example.models;

import jakarta.persistence.*;

/**
 * Cart items are products that exist in a cart, as products belong to shops already.
 * This class allows the manipulation of products to belong in carts.
 */
@Entity
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private int quantity;

    // Constructors
    public CartItem() {}

    public CartItem(Cart cart, Product product, int quantity) {
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
    }

    // Getters and Setters

    /**
     * Returns the Id of the cart item
     * @return Long the id of the cart item
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the id of the cart item
     * @param id a set id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the cart in which the items are in
     * @return Cart
     */
    public Cart getCart() {
        return cart;
    }

    /**
     * Sets the cart which the items are in
     * @param cart
     */
    public void setCart(Cart cart) {
        this.cart = cart;
    }

    /**
     * Get the product
     * @return the product
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Setter for the product
     * @param product
     */
    public void setProduct(Product product) {
        this.product = product;
    }

    /**
     * Getter for the number of product in the cart
     * @return int, number of x items in cart
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Setter for the number of items in the cart
     * @param quantity
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
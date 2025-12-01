package org.example.models;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password; // will be encoded using BCrypt

    private String name;



    @OneToOne(mappedBy="customer",cascade = CascadeType.ALL, orphanRemoval = true)
    private Cart cart;


    @Enumerated(EnumType.STRING)
    private UserType userType = UserType.CUSTOMER;

    // --- Constructors ---
    public Customer() {}

    public Customer(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
        Cart cart = new Cart(this);
        this.cart = cart;
    }

    // --- Getters & Setters ---

    /**
     * Getter for Customer id
     * @return the id of the Customer
     */
    public Long getId() { return id; }

    /**
     * Setter for merchant id
     * @param id of the merchant
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Getter for customer email
     * @return the email of the customer
     */
    public String getEmail() { return email; }

    /**
     * Setter for customer email
     * @param email of the customer
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * Getter for customer password
     * @return the password of the customer
     */
    public String getPassword() { return password; }

    /**
     * Setter for customer password
     * @param password of the customer
     */
    public void setPassword(String password) { this.password = password; }

    /**
     * Getter for customer Name
     * @return the name of the customer
     */
    public String getName() { return name; }

    /**
     * Setter for customer name
     * @param name of the customer
     */
    public void setName(String name) { this.name = name; }

    /**
     * Returns the type of user
     * @return UserType, the type of user
     */
    public UserType getUserType() {
        return this.userType;
    }

    /**
     * Getter for the customers cart
     * @return cart, the customers cart
     */
    public Cart getCart() {
        return cart;
    }

    /**
     * Setter for the customers cart
     * @param cart, used to set the cart
     */
    public void setCart(Cart cart) {
        this.cart = cart;
    }

}

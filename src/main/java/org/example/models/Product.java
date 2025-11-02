package org.example.models;

import jakarta.persistence.*;

@Entity
public class Product {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String description;
    private double price;
    private int stock;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;

    public Product() {
    }

    // Getters and Setters

    /**
     * Getter for product id
     * @return id of the product
     */
    public Long getId() {
        return id;
    }

    /**
     * Setter for product id
     * @param id the id of the product
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Getter for product name
     * @return the product name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for the name
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for the description
     * @return the description of the product
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter for the description
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Getter for the price
     * @return the price
     */
    public double getPrice() {
        return price;
    }

    /**
     * Setter for price
     * @param price the price to be set
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Getter for the stock
     * @return stock int of products in stock
     */
    public int getStock() {
        return stock;
    }

    /**
     * Setter for the stock
     * @param stock the number in stock (int)
     */
    public void setStock(int stock) {
        this.stock = stock;
    }

    /**
     * Getter for the shop
     * @return shop the shop in question
     */
    public Shop getShop() {
        return shop;
    }

    /**
     * Setter for the shop
     * @param shop the shop to set
     */
    public void setShop(Shop shop) {
        this.shop = shop;
    }
}

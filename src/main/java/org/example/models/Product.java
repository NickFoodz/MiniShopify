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

    // Getters and Setters

    // Getter for ID
    public Long getId() {
        return id;
    }
    // Setter for ID
    public void setId(Long id) {
        this.id = id;
    }

    // Getter for name
    public String getName() {
        return name;
    }
    // Setter for name
    public void setName(String name) {
        this.name = name;
    }

    // Getter for description
    public String getDescription() {
        return description;
    }
    // Setter for description
    public void setDescription(String description) {
        this.description = description;
    }

    // Getter for price
    public double getPrice() {
        return price;
    }
    // Setter for price
    public void setPrice(double price) {
        this.price = price;
    }

    // getter for stock
    public int getStock() {
        return stock;
    }
    // setter for stock
    public void setStock(int stock) {
        this.stock = stock;
    }

    // getter for shop
    public Shop getShop() {
        return shop;
    }
    // setter for shop
    public void setShop(Shop shop) {
        this.shop = shop;
    }
}

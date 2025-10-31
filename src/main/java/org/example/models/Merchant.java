package org.example.models;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Merchant {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String email;

    @OneToMany(mappedBy = "merchant", cascade = CascadeType.ALL)
    private List<Shop> shops;

    public Merchant() {
    }

    // Getters and Setters

    // Getter for ID
    public Long getId() {
        return this.id;
    }
    // Setter for ID
    public void setId(Long id) {
        this.id = id;
    }

    // Getter for name
    public String getName() {
        return this.name;
    }
    // Setter for name
    public void setName(String name) {
        this.name = name;
    }

    // Getter for email
    public String getEmail() {
        return this.email;
    }

    // Setter for email
    public void setEmail(String email) {
        this.email = email;
    }

    public List<Shop> getShops() {
        return this.shops;
    }

    public void setShops(List<Shop> shops) {
        this.shops = shops;
    }
}

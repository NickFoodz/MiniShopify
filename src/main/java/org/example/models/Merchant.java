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

    /**
     * Getter for merchant id
     * @return the id of the merchant
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Setter for the merchant id
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Getter for the merchant name
     * @return the name of the merchant
     */
    public String getName() {
        return this.name;
    }

    /**
     * Setter for the name
     * @param name the name to set to
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for the email
     * @return the email of the merchant
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Setter for the email
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Getter for the list of shops
     * @return the list of shops
     */
    public List<Shop> getShops() {
        return this.shops;
    }

    /**
     * Setter for the list of shops
     * @param shops a list of shops
     */
    public void setShops(List<Shop> shops) {
        this.shops = shops;
    }

    /**
     * Removes a shop from the shop list
     * @param shopName the name of the shop to remove
     */
    public void removeShop(String shopName) {
        //Even works if non-target shop name field is null (this was annoying)
        this.shops.removeIf(shop -> shop.getName() != null && shop.getName().equals(shopName));
    }
}

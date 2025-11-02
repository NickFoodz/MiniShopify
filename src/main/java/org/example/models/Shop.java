package org.example.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Shop {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> categories = new ArrayList<String>();

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL,orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Product> products = new ArrayList<Product>();

    @ManyToOne
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;

    public Shop() {}

    // Getters & Setters

    /**
     * Getter for the id
     * @return the id of this shop object
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Sets the ID for the shop object
     * @param id the id to set for the shop
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Getter for the name field of the shop object
     * @return the name of the shop
     */
    public String getName() {
        return this.name;
    }

    /**
     * Setter for the name field of the shop object
     * @param name the name of the shop
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for the categories list
     * @return the categories list
     */
    public List<String> getCategories() {
        return this.categories;
    }

    /**
     * Adds a category to the categories list
     * @param category the category to add
     */
    public void addCategory(String category) {
        categories.add(category);
    }

    /**
     * Getter for the merchant
     * @return the merchant
     */
    public Merchant getMerchant(){
        return this.merchant;
    }

    /**
     * Setter for the merchant
     * @param merchant the merchant to set for the shop
     */
    public void setMerchant(Merchant merchant){
        this.merchant = merchant;
    }

    /**
     * Getter for the list of products this shop has
     * @return the list of products the shop has
     */
    public List<Product> getProducts() {
        return this.products;
    }

    /**
     * Adds a product to the product list
     * @param product the product object to add to the shop
     */
    public void addProduct(Product product) {
        this.products.add(product);product.setShop(this);
    }

    /**
     * Remove a product by id from the shop's list of products
     * @param productId the id of the Product, stored in its name field
     */
    public void removeProduct(long productId) {
        this.products.removeIf(product -> product.getId() != null && product.getId().equals(productId));
    }
}



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

    @ElementCollection
    private List<String> categories = new ArrayList<String>();

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL)
    private List<Product> products = new ArrayList<Product>();

    @ManyToOne
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;

    public Shop() {}

    // Getters & Setters

    // Getter for id
    public Long getId() {
        return this.id;
    }

    // Setter for id
    public void setId(Long id) {
        this.id = id;
    }

    // Getter for store name
    public String getName() {
        return this.name;
    }

    // Setter for store name
    public void setName(String name) {
        this.name = name;
    }

    // Get categories
    public List<String> getCategories() {
        return this.categories;
    }

    // add categories
    public void addCategory(String category) {
        categories.add(category);
    }

    // Get merchant
    public Merchant getMerchant(){
        return this.merchant;
    }

    // Set merchant
    public void setMerchant(Merchant merchant){
        this.merchant = merchant;
    }

    // get products
    public List<Product> getProducts() {
        return this.products;
    }

    public void addProduct(Product product) {
        this.products.add(product);
    }
}



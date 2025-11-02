package org.example.controllers;

import org.example.models.Merchant;
import org.example.models.Product;
import org.example.models.Shop;
import org.example.repository.MerchantRepository;
import org.example.repository.ProductRepository;
import org.example.repository.ShopRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/gui")
public class GuiController {

    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;
    private final MerchantRepository merchantRepository;

    public GuiController(ShopRepository shopRepository, ProductRepository productRepository, MerchantRepository merchantRepository) {
        this.shopRepository = shopRepository;
        this.productRepository = productRepository;
        this.merchantRepository = merchantRepository;
    }

    // Home page
    @GetMapping("/")
    public String home() {
        return "index";
    }

    // Shops page
    @GetMapping("/shops")
    public String shops(Model model) {
        model.addAttribute("shops", shopRepository.findAll());
        return "shops";
    }

    // Adding a shop
    @PostMapping("/shops")
    public String addShop(@RequestParam String name) {
        Shop shop = new Shop();
        shop.setName(name);
        shopRepository.save(shop);
        return "redirect:/gui/shops";
    }

    // Add product page
    @GetMapping("/add-product")
    public String addProductForm() {
        return "add-product";
    }

    // Add product
    @PostMapping("/add-product")
    public String addProduct(@RequestParam String name,
                             @RequestParam String description,
                             @RequestParam double price,
                             @RequestParam int stock,
                             @RequestParam int shopID) {

        long id = shopID;

        if (shopRepository.findById(id).isPresent()){
            Product product = new Product();
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setStock(stock);
            Shop shop = shopRepository.findById(id).get();
            //productRepository.save(product);
            product.setShop(shop);
            shop.addProduct(product);
            shopRepository.save(shop);

            return "redirect:/gui/shops";
        } else {

            // some error redirect

            return "redirect:/error";

        }

        // After adding product, needs to be added to shop and displayed (Someone can work on this from here)
        // productRepository.save(product);
        // return "redirect:/gui/";
    }

    //Remove product
    @PostMapping("/remove-product")
    public String removeProduct(@RequestParam long productId,
                                @RequestParam long shopId) {

        try {
            Shop shop = shopRepository.findById(shopId).
                    orElseThrow(() -> new IllegalArgumentException("Shop not found"));
            Product product = productRepository.findById(productId).
                    orElseThrow(() -> new IllegalArgumentException("Product not found"));

            //Remove relationship
            shop.removeProduct(product.getName());
            //Delete the product
            productRepository.delete(product);
            shopRepository.save(shop);

            return "redirect:/gui/shops";
        }catch (IllegalArgumentException e){
            return "redirect:/error";
        }

    }

    //Remove Shop
    @PostMapping("/remove-shop")
    public String removeShop(@RequestParam long shopId) {
        //Need to add merchant id at some point if multiple exist
        try{
            //Add merchant here similar to below
            Shop shop = shopRepository.findById(shopId).
                    orElseThrow(() -> new IllegalArgumentException("Shop not found"));

            //Remove relationship from merchant (when eventually exists)
            //merchant.removeShop(shop.getName());
            //Delete Shop
            shopRepository.delete(shop);

            return "redirect:/gui/shops";

        } catch (IllegalArgumentException e){
            return "redirect:/error";
        }
    }

}
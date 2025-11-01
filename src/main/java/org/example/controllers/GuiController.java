package org.example.controllers;

import org.example.models.Product;
import org.example.models.Shop;
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

    public GuiController(ShopRepository shopRepository, ProductRepository productRepository) {
        this.shopRepository = shopRepository;
        this.productRepository = productRepository;
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
}
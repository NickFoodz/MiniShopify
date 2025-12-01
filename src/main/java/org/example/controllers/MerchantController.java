package org.example.controllers;

import jakarta.transaction.Transactional;
import org.example.models.Merchant;
import org.example.models.Product;
import org.example.models.Shop;
import org.example.repository.MerchantRepository;
import org.example.repository.ProductRepository;
import org.example.repository.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

@Controller
public class MerchantController {

    private final MerchantRepository merchantRepository;
    private final ShopRepository shopRepository; // assuming you have shops
    private final ProductRepository productRepository;

    public MerchantController(MerchantRepository merchantRepository,
                              ShopRepository shopRepository,
                              ProductRepository productRepository) {
        this.merchantRepository = merchantRepository;
        this.shopRepository = shopRepository;
        this.productRepository = productRepository;
    }

    /**
     * Adds attributes to merchant profile with the merchants details
     * @param model
     * @param principal, the principal user
     * @return the merchant profile page
     */
    @Transactional
    @GetMapping("/gui/merchant/profile")
    public String merchantProfile(Model model, Principal principal) {
        // Get the logged-in merchant's email
        String email = principal.getName();

        // Fetch the merchant
        Merchant merchant = merchantRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Merchant not found"));

        // Fetch the merchant's stores
        List<Shop> shops = shopRepository.findByMerchant(merchant);

        model.addAttribute("merchant", merchant);
        model.addAttribute("shops", shops);
        model.addAttribute("newShop", new Shop()); // <-- needed for the form

        return "merchant-profile"; // Thymeleaf template
    }

    /**
     * Handles the add shops request from the merchant profile
     * @param newShop, the new shop to be added
     * @param principal, the principal user (merchant)
     * @return the merchant profile page with the new shop
     */
    @PostMapping("/gui/merchant/add-shop")
    public String addShop(@ModelAttribute("newShop") Shop newShop, Principal principal) {
        String email = principal.getName();
        Merchant merchant = merchantRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Merchant not found"));

        newShop.setMerchant(merchant);
        shopRepository.save(newShop);

        return "redirect:/gui/merchant/profile"; // reload page after adding
    }

    @Autowired
    private FileUploadController fileUploadController;

    /**
     * Handles the adding product to shops request from merchant page
     * @param name, name of the product
     * @param shopId, shop id of the product
     * @param description, description of the product
     * @param price, price of the product
     * @param stock, stock of the product
     * @param imageUrl, imageUrl of the product
     * @param imageFile, imageFile of the product
     * @param user, the current user
     * @return the merchant profile page updated with new products
     * @throws IOException
     */
    @PostMapping("/gui/merchant/add-product")
    public String addProduct(
            @RequestParam("name") String name,
            @RequestParam("shopID") Long shopId,
            @RequestParam("description") String description,
            @RequestParam("price") Double price,
            @RequestParam("stock") Integer stock,
            @RequestParam(value = "imageUrl", required = false) String imageUrl,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @AuthenticationPrincipal UserDetails user,
            Model model
    ) throws IOException {
        // Find the merchant from the logged-in user
        var merchant = merchantRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new RuntimeException("Merchant not found"));

        try {
            // Find the shop AND make sure it belongs to this merchant
            Shop shop = shopRepository.findById(shopId)
                    .filter(s -> s.getMerchant().equals(merchant))
                    .orElseThrow(() -> new RuntimeException("Shop not found or not yours"));

            Product product = new Product();
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setStock(stock);
            product.setShop(shop);

            // Handle image: prioritize file upload over URL
            try {
                if (imageFile != null && !imageFile.isEmpty()) {
                    String uploadedImagePath = fileUploadController.saveUploadedFile(imageFile);
                    if (uploadedImagePath != null) {
                        product.setImageUrl(uploadedImagePath);
                    }
                } else if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                    product.setImageUrl(imageUrl);
                }
                // If neither provided, default image will be used from Product model
            } catch (IOException e) {
                List<Shop> shops = shopRepository.findByMerchant(merchant);
                model.addAttribute("errorMessage", e.getMessage());
                model.addAttribute("merchant", merchant);
                model.addAttribute("shops", shops);

                return "merchant-profile";

            }


            productRepository.save(product);

            return "redirect:/gui/merchant/profile";

        } catch (RuntimeException e) {
            List<Shop> shops = shopRepository.findByMerchant(merchant);

            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("merchant", merchant);
            model.addAttribute("shops", shops);

            return "merchant-profile";
        }
    }

    /**
     * Handles the remove shop request for a merchant
     * @param shopId, the shop Id to be removed
     * @param principal, the principal user (merchant)
     * @return the merchant profile with the updated list of shopp
     */
    @PostMapping("/gui/merchant/remove-shop")
    public String removeShop(@RequestParam Long shopId, Principal principal) {

        // Validate merchant
        Merchant merchant = merchantRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Merchant not found"));

        // Verify shop exists AND belongs to this merchant
        Shop shop = shopRepository.findById(shopId)
                .filter(s -> s.getMerchant().getId().equals(merchant.getId()))
                .orElseThrow(() -> new RuntimeException("Unauthorized attempt to delete shop"));

        // Delete the shop
        shopRepository.delete(shop);

        return "redirect:/gui/merchant/profile";
    }

    /**
     * Handles the reqyest to remove a product from a specific shop
     * @param productId, the product id that is to be removed
     * @param shopId, the shop id corresponding to the product
     * @param principal, the principal user (merchant)
     * @return the merchant profile page updated with the product removed
     */
    @PostMapping("/gui/merchant/remove-product")
    public String removeProduct(
            @RequestParam Long productId,
            @RequestParam Long shopId,
            Principal principal
    ) {

        // Validate merchant
        Merchant merchant = merchantRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Merchant not found"));

        // Validate shop belongs to merchant
        Shop shop = shopRepository.findById(shopId)
                .filter(s -> s.getMerchant().getId().equals(merchant.getId()))
                .orElseThrow(() -> new RuntimeException("Unauthorized shop"));

        // Validate the product exists in this shop
        Product product = productRepository.findById(productId)
                .filter(p -> p.getShop().getId().equals(shopId))
                .orElseThrow(() -> new RuntimeException("Invalid product for this shop"));

        shop.removeProduct(product.getId());
        productRepository.delete(product);
        shopRepository.save(shop);

        return "redirect:/gui/merchant/profile";
    }


}

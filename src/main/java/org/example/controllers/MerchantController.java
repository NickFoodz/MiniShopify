package org.example.controllers;

import org.example.models.Merchant;
import org.example.models.Shop;
import org.example.repository.MerchantRepository;
import org.example.repository.ShopRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@Controller
public class MerchantController {

    private final MerchantRepository merchantRepository;
    private final ShopRepository shopRepository; // assuming you have shops

    public MerchantController(MerchantRepository merchantRepository,
                              ShopRepository shopRepository) {
        this.merchantRepository = merchantRepository;
        this.shopRepository = shopRepository;
    }

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

        return "merchant-profile"; // Thymeleaf template
    }

}

package org.example.controllers;

import org.example.models.*;
import org.example.repository.CustomerRepository;
import org.example.repository.MerchantRepository;
import org.example.repository.ProductRepository;
import org.example.repository.ShopRepository;
import org.example.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/gui")
public class GuiController {

    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;
    private final MerchantRepository merchantRepository;
    private final CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public GuiController(ShopRepository shopRepository, ProductRepository productRepository, MerchantRepository merchantRepository, CustomerRepository customerRepository) {
        this.shopRepository = shopRepository;
        this.productRepository = productRepository;
        this.merchantRepository = merchantRepository;
        this.customerRepository = customerRepository;
    }

    // Home page
    @GetMapping("/")
    public String home() {
        return "index";
    }

    // Shops page (with search)
    @GetMapping("/shops")
    public String shops(@RequestParam(value = "q", required = false) String q,
                        @RequestParam(required = false) Map<String, String> params,
                        Model model) {

        // Extract search parameter and remove it from params
        Map<String, String> sortMap = new HashMap<>();
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (!entry.getKey().equals("q")) {
                    sortMap.put(entry.getKey(), entry.getValue());
                }
            }
        }

        // --- SEARCH LOGIC ---
        List<Shop> shops;
        if (q == null || q.trim().isEmpty()) {
            shops = (List<Shop>) shopRepository.findAll();
        } else {
            shops = shopRepository.findByNameContainingIgnoreCase(q.trim());
        }

        // --- PER-SHOP SORTING LOGIC ---
        for (Shop shop : shops) {
            String sortBy = sortMap.getOrDefault(String.valueOf(shop.getId()), "name-asc");
            List<Product> sortedProducts = sortProducts(shop.getProducts(), sortBy);

            shop.getProducts().clear();
            shop.getProducts().addAll(sortedProducts);
        }

        // Add to model
        model.addAttribute("shops", shops);
        model.addAttribute("q", q);
        model.addAttribute("sortMap", sortMap);

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



    /**
     * Remove product from the shop
     *
     * @param productId the product id to remove
     * @param shopId    the shop id to remove product from
     * @return back to the shops page
     */
    @PostMapping("/remove-product")
    public String removeProduct(@RequestParam long productId,
                                @RequestParam long shopId,
                                RedirectAttributes redirectAttributes) {

        try {
            Shop shop = shopRepository.findById(shopId).
                    orElseThrow(() -> new IllegalArgumentException("Shop not found"));
            Product product = productRepository.findById(productId).
                    orElseThrow(() -> new IllegalArgumentException("Product not found"));

            //Remove relationship
            shop.removeProduct(product.getId());
            //Delete the product
            productRepository.delete(product);
            shopRepository.save(shop);

            return "redirect:/gui/shops";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            redirectAttributes.addFlashAttribute("path", "Path: /remove-product");
            return "redirect:/gui/custom-error";
        }

    }

    /**
     * Removes a shop from the repository.
     *
     * @param shopId the shop id to remove
     * @return to the shops page
     */
    @PostMapping("/remove-shop")
    public String removeShop(@RequestParam long shopId, RedirectAttributes redirectAttributes) {
        //Need to add merchant id at some point if multiple exist
        try {
            //Add merchant here similar to below
            Shop shop = shopRepository.findById(shopId).
                    orElseThrow(() -> new IllegalArgumentException("Shop not found"));

            //Remove relationship from merchant (when eventually exists)
            //merchant.removeShop(shop.getName());
            //Delete Shop
            shopRepository.delete(shop);

            return "redirect:/gui/shops";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            redirectAttributes.addFlashAttribute("path", "Path: /remove-shop");
            return "redirect:/gui/custom-error";
        }
    }

    @GetMapping("/shop/{id}")
    public String viewShop(@PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            if (shopRepository.findById(id).isEmpty()) {
                throw new IllegalArgumentException("No shop found");
            }
            Shop shop = shopRepository.findById(id).get();
            model.addAttribute("shop", shop);
            model.addAttribute("products", shop.getProducts());
            return "shop";
        } catch (Exception e) {
            ra.addFlashAttribute("message", e.getMessage());
            return "redirect:/gui/custom-error";
        }
    }

    /**
     * Sort products based on criteria
     *
     * @param products List of products to sort
     * @param sortBy   Sorting criteria
     * @return Sorted list of products
     */
    private List<Product> sortProducts(List<Product> products, String sortBy) {
        Comparator<Product> comparator;

        if (products == null) {
            return null;
        }
        if (sortBy == null) {
            comparator = Comparator.comparing(Product::getName);
        } else {
            switch (sortBy.toLowerCase()) {
                case "price-asc":
                    comparator = Comparator.comparing(Product::getPrice);
                    break;
                case "price-desc":
                    comparator = Comparator.comparing(Product::getPrice).reversed();
                    break;
                case "name-desc":
                    comparator = Comparator.comparing(Product::getName).reversed();
                    break;
                case "stock":
                    comparator = Comparator.comparing(Product::getStock).reversed();
                    break;
                case "name-asc":
                default:
                    comparator = Comparator.comparing(Product::getName);
                    break;
            }
        }

        return products.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    @GetMapping("/custom-error")
    public String showErrorPage() {
        return "custom-error";
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // Looks for login.html in templates/
    }

    @GetMapping("/register/merchant")
    public String registerMerchant(Model model) {
        model.addAttribute("merchant", new Merchant());
        return "register-merchant";
    }

    @PostMapping("/register/merchant")
    public String processMerchant(Merchant merchant) {
        merchant.setPassword(passwordEncoder.encode(merchant.getPassword()));
        merchantRepository.save(merchant);

        return "redirect:/gui/login?registered";

    }

    @GetMapping("/register/customer")
    public String registerCustomer(Model model) {
        model.addAttribute("customer", new Customer());
        return "register-customer";
    }

    @PostMapping("/register/customer")
    public String processCustomer(Customer customer) {
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        customerRepository.save(customer);

        return "redirect:/gui/login?registered";
    }

    @GetMapping("/dashboard")
    public String dashboardRedirect(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        if (userDetails.getUserType() == UserType.MERCHANT) {
            return "redirect:/gui/merchant/profile";
        } else if (userDetails.getUserType() == UserType.CUSTOMER) {
            return "redirect:/gui/customer/profile";
        } else {
            throw new RuntimeException("Unknown user type");
        }
    }

}
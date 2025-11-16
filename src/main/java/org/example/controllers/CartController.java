package org.example.controllers;

import org.example.models.Cart;
import org.example.models.CartItem;
import org.example.models.Customer;
import org.example.models.Product;
import org.example.repository.CartRepository;
import org.example.repository.CustomerRepository;
import org.example.repository.ProductRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CartController {

    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;

    public CartController(CustomerRepository customerRepository,
                          ProductRepository productRepository,
                          CartRepository cartRepository) {
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
    }

    /**
     * Display the customer's cart
     */
    @GetMapping("/gui/customer/cart")
    public String viewCart(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/gui/login";
        }

        String email = authentication.getName();
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Cart cart = customer.getCart();
        if (cart == null) {
            cart = new Cart(customer);
            customer.setCart(cart);
            customerRepository.save(customer);
        }

        model.addAttribute("cart", cart);
        model.addAttribute("cartItems", cart.getCartItems());
        model.addAttribute("total", cart.calculateTotal());

        return "cart";
    }

    /**
     * Add a product to the cart
     */
    @PostMapping("/gui/customer/cart/add")
    public String addToCart(@RequestParam Long productId,
                            @RequestParam(defaultValue = "1") int quantity,
                            Authentication authentication,
                            RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/gui/login";
        }

        try {
            String email = authentication.getName();
            Customer customer = customerRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            Cart cart = customer.getCart();
            if (cart == null) {
                cart = new Cart(customer);
                customer.setCart(cart);
            }

            boolean success = cart.addProduct(product, quantity);

            if (success) {
                cartRepository.save(cart);
                productRepository.save(product);
                redirectAttributes.addFlashAttribute("message",
                        "Added " + quantity + " x " + product.getName() + " to cart!");
            } else {
                redirectAttributes.addFlashAttribute("error",
                        "Not enough stock available for " + product.getName());
            }

            return "redirect:/gui/shops";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Failed to add product to cart: " + e.getMessage());
            return "redirect:/gui/shops";
        }
    }

    /**
     * Remove a product from the cart
     */
    @PostMapping("/gui/customer/cart/remove")
    public String removeFromCart(@RequestParam Long productId,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/gui/login";
        }

        try {
            String email = authentication.getName();
            Customer customer = customerRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            Cart cart = customer.getCart();
            boolean success = cart.removeProduct(product);

            if (success) {
                cartRepository.save(cart);
                productRepository.save(product);
                redirectAttributes.addFlashAttribute("message",
                        "Removed " + product.getName() + " from cart");
            } else {
                redirectAttributes.addFlashAttribute("error",
                        "Product not found in cart");
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Failed to remove product: " + e.getMessage());
        }

        return "redirect:/gui/customer/cart";
    }

    /**
     * Update quantity of a product in cart
     */
    @PostMapping("/gui/customer/cart/update")
    public String updateQuantity(@RequestParam Long productId,
                                 @RequestParam int quantity,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/gui/login";
        }

        try {
            String email = authentication.getName();
            Customer customer = customerRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            Cart cart = customer.getCart();

            // Find the cart item
            CartItem item = cart.getCartItems().stream()
                    .filter(ci -> ci.getProduct().getId().equals(productId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Product not in cart"));

            int currentQuantity = item.getQuantity();
            int difference = quantity - currentQuantity;

            if (difference > 0) {
                // Need to add more
                if (!product.removeStock(difference)) {
                    redirectAttributes.addFlashAttribute("error",
                            "Not enough stock available");
                    return "redirect:/gui/customer/cart";
                }
                item.setQuantity(quantity);
            } else if (difference < 0) {
                // Need to reduce
                product.addStock(Math.abs(difference));
                item.setQuantity(quantity);
            }

            cartRepository.save(cart);
            productRepository.save(product);
            redirectAttributes.addFlashAttribute("message", "Cart updated");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Failed to update cart: " + e.getMessage());
        }

        return "redirect:/gui/customer/cart";
    }

    /**
     * Clear the entire cart
     */
    @PostMapping("/gui/customer/cart/clear")
    public String clearCart(Authentication authentication,
                            RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/gui/login";
        }

        try {
            String email = authentication.getName();
            Customer customer = customerRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            Cart cart = customer.getCart();
            cart.clearCart();
            cartRepository.save(cart);

            redirectAttributes.addFlashAttribute("message", "Cart cleared");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Failed to clear cart: " + e.getMessage());
        }

        return "redirect:/gui/customer/cart";
    }
}
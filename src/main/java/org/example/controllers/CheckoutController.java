package org.example.controllers;

import org.example.models.Cart;
import org.example.models.Customer;
import org.example.repository.CartRepository;
import org.example.repository.CustomerRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CheckoutController {

    private final CustomerRepository customerRepository;
    private final CartRepository cartRepository;

    public CheckoutController(CustomerRepository customerRepository,
                              CartRepository cartRepository) {
        this.customerRepository = customerRepository;
        this.cartRepository = cartRepository;
    }

    /**
     * Display the checkout page with customer's cart and checkout form
     */
    @GetMapping("/gui/customer/checkout")
    public String showCheckoutPage(Model model, Authentication authentication,
                                   RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/gui/login";
        }

        try {
            String email = authentication.getName();
            Customer customer = customerRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            Cart cart = customer.getCart();
            if (cart == null || cart.getCartItems().isEmpty()) {
                redirectAttributes.addFlashAttribute("error",
                        "Your cart is empty. Please add items before checking out.");
                return "redirect:/gui/customer/cart";
            }

            double total = cart.calculateTotal();
            double tax = total * 0.13; // 13% tax
            double grandTotal = total + tax;

            model.addAttribute("customer", customer);
            model.addAttribute("cart", cart);
            model.addAttribute("cartItems", cart.getCartItems());
            model.addAttribute("subtotal", total);
            model.addAttribute("tax", tax);
            model.addAttribute("grandTotal", grandTotal);

            return "checkout";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error loading checkout page: " + e.getMessage());
            return "redirect:/gui/customer/cart";
        }
    }

    /**
     * Process the checkout order
     */
    @PostMapping("/gui/customer/checkout/process")
    public String processCheckout(@RequestParam String fullName,
                                  @RequestParam String email,
                                  @RequestParam String address,
                                  @RequestParam String city,
                                  @RequestParam String province,
                                  @RequestParam String postalCode,
                                  @RequestParam String cardNumber,
                                  @RequestParam String cardName,
                                  @RequestParam String expiryDate,
                                  @RequestParam String cvv,
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/gui/login";
        }

        try {
            String customerEmail = authentication.getName();
            Customer customer = customerRepository.findByEmail(customerEmail)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            Cart cart = customer.getCart();
            if (cart == null || cart.getCartItems().isEmpty()) {
                redirectAttributes.addFlashAttribute("error",
                        "Your cart is empty.");
                return "redirect:/gui/customer/cart";
            }

            // Validate form inputs
            if (fullName.isBlank() || email.isBlank() || address.isBlank() ||
                    city.isBlank() || province.isBlank() || postalCode.isBlank() ||
                    cardNumber.isBlank() || cardName.isBlank() ||
                    expiryDate.isBlank() || cvv.isBlank()) {
                redirectAttributes.addFlashAttribute("error",
                        "Please fill in all required fields.");
                return "redirect:/gui/customer/checkout";
            }

            // Validate card number (basic validation - 16 digits)
            String cleanCardNumber = cardNumber.replaceAll("\\s+", "");
            if (!cleanCardNumber.matches("\\d{16}")) {
                redirectAttributes.addFlashAttribute("error",
                        "Invalid card number. Please enter a 16-digit card number.");
                return "redirect:/gui/customer/checkout";
            }

            // Validate CVV (3 or 4 digits)
            if (!cvv.matches("\\d{3,4}")) {
                redirectAttributes.addFlashAttribute("error",
                        "Invalid CVV. Please enter a 3 or 4 digit CVV.");
                return "redirect:/gui/customer/checkout";
            }

            // Validate expiry date format (MM/YY)
            if (!expiryDate.matches("(0[1-9]|1[0-2])/\\d{2}")) {
                redirectAttributes.addFlashAttribute("error",
                        "Invalid expiry date. Please use MM/YY format.");
                return "redirect:/gui/customer/checkout";
            }

            // Calculate order totals
            double subtotal = cart.calculateTotal();
            double tax = subtotal * 0.13;
            double grandTotal = subtotal + tax;


            // Simulate successful checkout
            // Clear the cart after successful checkout
            cart.getCartItems().clear();
            cartRepository.save(cart);

            // Add success message with order details
            redirectAttributes.addFlashAttribute("message",
                    String.format("Order placed successfully! Total: $%.2f. " +
                            "A confirmation email has been sent to %s.", grandTotal, email));
            redirectAttributes.addFlashAttribute("orderTotal", grandTotal);
            redirectAttributes.addFlashAttribute("orderEmail", email);

            return "redirect:/gui/customer/checkout/confirmation";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error processing checkout: " + e.getMessage());
            return "redirect:/gui/customer/checkout";
        }
    }

    /**
     * Display order confirmation page
     */
    @GetMapping("/gui/customer/checkout/confirmation")
    public String showConfirmation(Model model, Authentication authentication,
                                   RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/gui/login";
        }

        // If no order info in flash attributes, redirect to shops
        if (!model.containsAttribute("message")) {
            return "redirect:/gui/shops";
        }

        return "checkout-confirmation";
    }
}
package org.example.controllers;

import org.example.models.Product;
import org.example.repository.ProductRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/gui/search")
public class SearchController {

    private final ProductRepository productRepo;

    public SearchController(ProductRepository productRepo) {
        this.productRepo = productRepo;
    }

    /**
     * Handles the search mechanism when looking for specific products
     * @param q, the Query used to find products
     * @param model, the current model
     * @return the list of products matching the description
     */
    @GetMapping
    public String search(@RequestParam(required = false) String q, Model model) {

        List<Product> results = (q == null || q.isBlank())
                ? List.of()
                : productRepo.searchProducts(q);

        model.addAttribute("query", q);
        model.addAttribute("results", results);

        return "search";
    }
}

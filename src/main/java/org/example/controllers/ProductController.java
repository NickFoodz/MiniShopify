package org.example.controllers;

import org.example.repository.ProductRepository;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {

    private ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
}

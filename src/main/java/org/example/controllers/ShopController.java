package org.example.controllers;

import org.example.repository.ShopRepository;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShopController {

    private ShopRepository shopRepository;

    public ShopController(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }
}

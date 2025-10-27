package org.example.controllers;

import org.example.repository.MerchantRepository;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MerchantController {

    private MerchantRepository merchantRepository;

    public MerchantController(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }
}

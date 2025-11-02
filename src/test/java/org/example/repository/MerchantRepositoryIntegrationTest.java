package org.example.repository;

import org.example.ShopAppApplication;
import org.example.models.Merchant;
import org.example.models.Shop;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for MerchantRepository
 * Tests merchant CRUD operations and relationships
 */
@SpringBootTest(classes = ShopAppApplication.class)
class MerchantRepositoryIntegrationTest {

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private ShopRepository shopRepository;

    @AfterEach
    void cleanup() {
        shopRepository.deleteAll();
        merchantRepository.deleteAll();
    }

    @Test
/**
 * Tests the behavior of testSaveAndFindMerchant().
 */
    void testSaveAndFindMerchant() {
        Merchant merchant = new Merchant();
        merchant.setName("John Doe");
        merchant.setEmail("john.doe@example.com");

        Merchant saved = merchantRepository.save(merchant);

        // Validate expected outcomes
        assertNotNull(saved.getId());
        // Validate expected outcomes
        assertEquals("John Doe", saved.getName());
        // Validate expected outcomes
        assertEquals("john.doe@example.com", saved.getEmail());

        Optional<Merchant> found = merchantRepository.findById(saved.getId());
        // Validate expected outcomes
        assertTrue(found.isPresent());
        // Validate expected outcomes
        assertEquals("john.doe@example.com", found.get().getEmail());
    }

    @Test
/**
 * Tests the behavior of testMerchantWithShops().
 */
    void testMerchantWithShops() {
        Merchant merchant = new Merchant();
        merchant.setName("Jane Smith");
        merchant.setEmail("jane@example.com");

        Merchant savedMerchant = merchantRepository.save(merchant);

        Shop shop1 = new Shop();
        shop1.setName("Shop 1");
        shop1.setMerchant(savedMerchant);

        Shop shop2 = new Shop();
        shop2.setName("Shop 2");
        shop2.setMerchant(savedMerchant);

        shopRepository.save(shop1);
        shopRepository.save(shop2);

        Optional<Merchant> found = merchantRepository.findById(savedMerchant.getId());
        // Validate expected outcomes
        assertTrue(found.isPresent());
        // Validate expected outcomes
        assertNotNull(found.get().getShops());
    }

    @Test
/**
 * Tests the behavior of testUpdateMerchant().
 */
    void testUpdateMerchant() {
        Merchant merchant = new Merchant();
        merchant.setName("Bob Johnson");
        merchant.setEmail("bob@example.com");

        Merchant saved = merchantRepository.save(merchant);

        saved.setEmail("bob.johnson@newdomain.com");
        merchantRepository.save(saved);

        Optional<Merchant> updated = merchantRepository.findById(saved.getId());
        // Validate expected outcomes
        assertTrue(updated.isPresent());
        // Validate expected outcomes
        assertEquals("bob.johnson@newdomain.com", updated.get().getEmail());
    }

    @Test
/**
 * Tests the behavior of testDeleteMerchant().
 */
    void testDeleteMerchant() {
        Merchant merchant = new Merchant();
        merchant.setName("Alice Williams");
        merchant.setEmail("alice@example.com");

        Merchant saved = merchantRepository.save(merchant);
        Long merchantId = saved.getId();

        merchantRepository.deleteById(merchantId);

        // Validate expected outcomes
        assertFalse(merchantRepository.findById(merchantId).isPresent());
    }
}
package org.example.models;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Merchant model
 * @author Nick Fuda
 * @version 1.0
 */
class MerchantTest {

    private Merchant testMerchant;
    private List<Shop> testShops;

    @BeforeEach
    void setUp() {
        testMerchant = new Merchant();
        testMerchant.setId(1L);
        testMerchant.setName("TestMerchant");
        testMerchant.setEmail("TestEmail");
        testShops = new ArrayList<>();
        Shop shop1 = new Shop();
        Shop shop2 = new Shop();
        testShops.add(shop1);
        testShops.add(shop2);
        testMerchant.setShops(testShops);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getId() {
        assertEquals(1L, testMerchant.getId());
    }

    @Test
    void setId() {
        testMerchant.setId(2L);
        assertEquals(2L, testMerchant.getId());
    }

    @Test
    void getName() {
        assertEquals("TestMerchant", testMerchant.getName());
    }

    @Test
    void setName() {
        testMerchant.setName("TestName");
        assertEquals("TestName", testMerchant.getName());
    }

    @Test
    void getEmail() {
        assertEquals("TestEmail", testMerchant.getEmail());
    }

    @Test
    void setEmail() {
        testMerchant.setEmail("TestEmail2");
        assertEquals("TestEmail2", testMerchant.getEmail());
    }

    @Test
    void getShops() {
        assertSame(testShops, testMerchant.getShops());

    }

    @Test
    void setShops() {
        List<Shop> testShops2 = new ArrayList();
        testShops2.add(new Shop());
        testMerchant.setShops(testShops2);
        assertSame(testShops2, testMerchant.getShops());
    }

    @Test
    void removeShop() {
        Shop testShop = new Shop();
        testShop.setName("TestShop");
        System.out.println(testShop.getName());
        assertEquals("TestShop", testShop.getName());
        testShops.add(testShop);
        assertTrue(testShops.contains(testShop));
        assertTrue(testMerchant.getShops().contains(testShop));

        testMerchant.removeShop("TestShop");



    }
}
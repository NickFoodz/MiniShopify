package org.example;

import com.github.javafaker.Faker;
import org.example.models.Merchant;
import org.example.models.Product;
import org.example.models.Shop;
import org.example.repository.MerchantRepository;
import org.example.repository.ProductRepository;
import org.example.repository.ShopRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@Configuration
public class    ShopAppApplication {
    private static final Logger log = LoggerFactory.getLogger(ShopAppApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ShopAppApplication.class, args);
    }

    /**
     * Adds link to spring application to click and view localhost of GUI easily
     * @return log args
     */
    @Bean
    public CommandLineRunner linkToLocalHost() {
        return args -> {
            log.info("\n=== Application Ready ===");
            log.info("Web interface: http://localhost:8080/gui");
        };
    }

    /**
     * Uses Faker to generate random merchants, shops, and products
     * @return log args
     */
//    @Bean
//    public CommandLineRunner loadFakerData(MerchantRepository merchantRepo,
//                                           ShopRepository shopRepo,
//                                           ProductRepository productRepo) {
//        return args -> {
//            Faker faker = new Faker();
//            Random random = new Random();
//            PasswordEncoder encoder = new BCryptPasswordEncoder();
//
//            productRepo.deleteAll();
//            shopRepo.deleteAll();
//            merchantRepo.deleteAll();
//
//            List<Merchant> merchants = new ArrayList<>();
//
//            for (int i = 0; i < 10; i++) {
//                Merchant merchant = new Merchant();
//                merchant.setName(faker.company().name());
//                merchant.setEmail("merchant" + i + "_" + faker.internet().emailAddress());
//                merchant.setPassword(encoder.encode("password"));
//
//                List<Shop> shops = new ArrayList<>();
//                for (int j = 0; j < 3; j++) {
//                    Shop shop = new Shop();
//                    shop.setName(faker.company().industry() + " Shop " + (j + 1));
//                    shop.setMerchant(merchant);
//
//                    List<Product> products = new ArrayList<>();
//                    for (int k = 0; k < 5; k++) {
//                        Product product = new Product();
//                        product.setName(faker.commerce().productName());
//                        product.setDescription(faker.lorem().sentence(8));
//                        product.setPrice(Double.parseDouble(faker.commerce().price(10.0, 500.0)));
//                        product.setStock(random.nextInt(50) + 1);
//                        shop.addProduct(product);
//                        products.add(product);
//                    }
//
//                    shops.add(shop);
//                }
//
//                merchant.setShops(shops);
//                merchants.add(merchant);
//            }
//
//            merchantRepo.saveAll(merchants);
//
//            log.info("💾 Faker data successfully loaded:");
//            log.info("→ 10 merchants, 30 shops, 150 products created.");
//            log.info("Web interface: http://localhost:8080/gui");
//
//        };
//    }
}

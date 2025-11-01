package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
public class ShopAppApplication {
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
            log.info("Web interface: http://localhost:8080/");
        };
    }
}

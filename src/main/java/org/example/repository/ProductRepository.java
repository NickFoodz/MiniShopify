package org.example.repository;

import org.example.models.Merchant;
import org.example.models.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface ProductRepository extends CrudRepository<Product, Long> {

    @Query("""
        SELECT p FROM Product p
        WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(p.shop.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    List<Product> searchProducts(String keyword);
}

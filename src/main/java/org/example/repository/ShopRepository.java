package org.example.repository;

import org.example.models.Merchant;
import org.example.models.Shop;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface ShopRepository extends CrudRepository<Shop, Long> {

    List<Shop> findByMerchant(Merchant merchant);

    List<Shop> findByNameContainingIgnoreCase(String name);
}

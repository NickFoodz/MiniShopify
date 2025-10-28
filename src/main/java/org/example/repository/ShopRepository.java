package org.example.repository;

import org.example.models.Merchant;
import org.example.models.Shop;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface ShopRepository extends CrudRepository<Shop, Long> {
}

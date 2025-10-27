package org.example.repository;

import org.example.models.Merchant;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface MerchantRepository extends CrudRepository<Merchant, Long> {
}

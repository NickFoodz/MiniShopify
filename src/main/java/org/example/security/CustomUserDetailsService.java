package org.example.security;

import org.example.models.Customer;
import org.example.models.Merchant;
import org.example.repository.CustomerRepository;
import org.example.repository.MerchantRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MerchantRepository merchantRepository;
    private final CustomerRepository customerRepository;

    // Constructor injection ensures Spring injects the repositories
    public CustomUserDetailsService(MerchantRepository merchantRepository,
                                    CustomerRepository customerRepository) {
        this.merchantRepository = merchantRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // Check if user is a merchant
        Optional<Merchant> merchant = merchantRepository.findByEmail(email);
        if (merchant.isPresent()) {
            return new CustomUserDetails(merchant.get());
        }

        // Check if user is a customer
        Optional<Customer> customer = customerRepository.findByEmail(email);
        if (customer.isPresent()) {
            return new CustomUserDetails(customer.get());
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}

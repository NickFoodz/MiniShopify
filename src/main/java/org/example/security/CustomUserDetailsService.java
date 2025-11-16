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


/**
 * Custom implementation of {@link org.springframework.security.core.userdetails.UserDetailsService}
 * that allows Spring Security to load user information during authentication.
 *
 * This service checks two different user types — {@code Merchant} and {@code Customer} —
 * in order to support login for both account types using their email address.
 *
 * Spring Security calls {@link #loadUserByUsername(String)} automatically when a user
 * attempts to log in. The returned {@link UserDetails} object is then used for verifying the
 * password and building the authenticated SecurityContext.
 *
 * This class relies on constructor-based dependency injection to ensure that the
 * repositories are always initialized and to avoid NullPointerExceptions.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MerchantRepository merchantRepository;
    private final CustomerRepository customerRepository;

    // Constructor injection ensures Spring injects the repositories
    /**
     * Creates the service with the required repositories.
     *
     * @param merchantRepository repository used to look up merchants by email
     * @param customerRepository repository used to look up customers by email
     */
    public CustomUserDetailsService(MerchantRepository merchantRepository,
                                    CustomerRepository customerRepository) {
        this.merchantRepository = merchantRepository;
        this.customerRepository = customerRepository;
    }

    /**
     * Loads a user by email. Spring Security calls this method automatically during login.
     *
     * The authentication provider will use the returned {@link UserDetails} object to
     * validate the password stored in the database.
     *
     * @param email the email provided by the user during login
     * @return a {@link UserDetails} object containing the authenticated user's information
     * @throws UsernameNotFoundException if no Merchant or Customer exists with the given email
     */
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

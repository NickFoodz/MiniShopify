package org.example.security;

import org.example.models.Customer;
import org.example.models.Merchant;
import org.example.models.UserType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Wrapper class that adapts both Merchant and Customer entities into a standard
 * {@link org.springframework.security.core.userdetails.UserDetails} object.
 *
 * Spring Security uses this class to access account information such as password,
 * authorities (roles), and whether the account is enabled.
 *
 * By providing a single implementation for both user types, the application can
 * authenticate multiple domain models using the same security layer.
 */
public class CustomUserDetails implements UserDetails {

    private String email;
    private String password;
    private UserType userType;

    /**
     * Creates a UserDetails object for merchants.
     */
    public CustomUserDetails(Merchant merchant){
        this.email = merchant.getEmail();
        this.password = merchant.getPassword();
        this.userType = merchant.getUserType();
    }

    /**
     * Creates a UserDetails object for customers.
     */
    public CustomUserDetails(Customer customer){
        this.email = customer.getEmail();
        this.password = customer.getPassword();
        this.userType = customer.getUserType();
    }


    /**
     * Returns the collection of authorities (roles) granted to the user.
     *
     * Spring Security requires each role to be represented as a
     * {@link SimpleGrantedAuthority} and prefixed with {@code "ROLE_"}.
     * This method maps the user's {@code userType} enum (e.g., CUSTOMER, MERCHANT)
     * into a corresponding authority such as {@code ROLE_CUSTOMER} or
     * {@code ROLE_MERCHANT}.
     *
     * These authorities are used by Spring Security during authorization checks,
     * such as {@code hasRole("CUSTOMER")} or {@code hasRole("MERCHANT")} defined
     * in the SecurityConfig.
     *
     * @return a collection containing exactly one {@link SimpleGrantedAuthority}
     *         representing the user's role.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_"  + userType.name()));
    }

    /**
     * Returns the password of the user
     * @return String password
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Returns the username (email) of the user
     * @return String email
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * Returns the user type
     * @return
     */
    public UserType getUserType() {
        return userType;
    }

    /**
     * Checks to see if the account is not expired
     * @return Boolean value to determine if account is expired or not, true if yes
     */
    @Override
    public boolean isAccountNonExpired() { return true; }

    /**
     * Checks to see if credentials are expired or not
     * @return Boolean value to see if credentials are valid, true if yes
     */
    @Override
    public boolean isCredentialsNonExpired() { return true; }

    /**
     * Checks to see if the account is enabled and not deactivated
     * @return Boolean value to determine if the account is enabled, true if yes
     */
    @Override
    public boolean isEnabled() { return true; }
}

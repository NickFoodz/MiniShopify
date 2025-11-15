package org.example.security;

import org.example.models.Customer;
import org.example.models.Merchant;
import org.example.models.UserType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private String email;
    private String password;
    private UserType userType;

    public CustomUserDetails(Merchant merchant){
        this.email = merchant.getEmail();
        this.password = merchant.getPassword();
        this.userType = merchant.getUserType();
    }

    public CustomUserDetails(Customer customer){
        this.email = customer.getEmail();
        this.password = customer.getPassword();
        this.userType = customer.getUserType();
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_"  + userType.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}

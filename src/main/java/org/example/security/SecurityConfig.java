package org.example.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {

        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/gui","/gui/login", "/gui/register/**", "/", "/gui/shops", "/gui/",
                                "/gui/register/customer", "/gui/register/merchant").permitAll()

                        // merchant only pages
                        .requestMatchers("/gui/merchant/**").hasRole("MERCHANT")

                        // customer only pages
                        .requestMatchers("/gui/customer/**").hasRole("CUSTOMER")

                        // any other request
                        .anyRequest().authenticated()
        )
                .formLogin(login -> login
                        .loginPage("/gui/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/gui/", true)
                        .permitAll())
                .logout(logout -> logout.logoutUrl("/gui/logout")
                        .logoutSuccessUrl("/gui/login?logout")
                        .permitAll()
                );

        return http.build();
    }

}

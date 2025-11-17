package org.example.security;


import jakarta.servlet.http.HttpServletRequest;
import org.example.models.UserType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.DispatcherType;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.web.filter.ForwardedHeaderFilter;
import org.springframework.core.Ordered;


/**
 * Central configuration class for Spring Security.
 * <p>
 * This class defines all security-related behaviors for the application,
 * including password encoding, authentication handling, request authorization,
 * login flow, and logout behavior. Spring Boot automatically detects this class
 * because it is annotated with {@code @EnableWebSecurity}.
 * The configuration is structured around three main components:
 * PasswordEncoder — responsible for hashing and verifying passwords.</li>
 * AuthenticationManager— performs the authentication process by delegating to the
 * configured authentication mechanism (such as a custom UserDetailsService)
 * SecurityFilterChain— defines URL access rules, form login setup and logout behavior.
 * <p>
 * <p>
 * Spring Security applies these settings to every incoming HTTP request to determine
 * authentication requirements and authorization rules.</p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    /**
     * Defines the password encoder used to hash and verify passwords.
     * BCrypt is the recommended algorithm by Spring Security because it:
     * Is slow by design, reducing brute-force attacks
     * Automatically salts passwords
     * Supports multiple hashing rounds
     *
     * @return a BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures and exposes the {@link AuthenticationManager} as a Spring bean.
     * <p>
     * The AuthenticationManager is the central Spring Security component responsible
     * for performing authentication attempts (such as verifying email/password credentials).
     * Spring Security automatically uses this bean during form login processing.</p>
     * <p>
     * By delegating to {@link AuthenticationConfiguration#getAuthenticationManager()},
     * we allow Spring to build the AuthenticationManager using all registered
     * authentication providers, such as:
     * <p>
     * Password encoder configuration
     *
     * @param config The authentication configuration provided by Spring, containing
     *               the fully initialized authentication setup.
     * @return The AuthenticationManager used by Spring Security to authenticate users.
     * @throws Exception If the AuthenticationManager cannot be created.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {

        return config.getAuthenticationManager();
    }

    /**
     * Defines the security configuration for the entire web application by building
     * and returning a {@link SecurityFilterChain}. The SecurityFilterChain determines
     * how HTTP requests are secured, what URLs require authentication, and how login
     * and logout operations behave.
     * <p>
     * This configuration includes:
     * <p>
     * Disabling CSRF— useful during development or when the app
     * does not use traditional form submissions.
     * <p>
     * Authorization rules — publicly accessible pages, merchant-only
     * pages, customer-only pages, and secure fallback rules.
     * <p>
     * Form login configuration — custom login page, custom username
     * parameter ("email"), and redirect behavior after successful authentication.
     * <p>
     * Logout configuration</strong> — custom logout URL and redirect path.
     * <p>
     * <p>
     * Spring Security applies these rules to every incoming HTTP request to determine
     * how access should be granted or denied.
     *
     * @param http The {@link HttpSecurity} builder used to customize web security.
     * @return A fully built {@link SecurityFilterChain} containing all configured rules.
     * @throws Exception If an error occurs while building the security filter chain.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())


                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/gui",
                                "/gui/",
                                "/gui/login",
                                "/gui/register/**",
                                "/gui/shops",
                                "/gui/search",
                                "/css/**",
                                "/gui/add-product"
                        ).permitAll()

                        // merchant only pages
                        .requestMatchers("/gui/merchant/**").hasRole("MERCHANT")

                        // customer only pages
                        .requestMatchers("/gui/customer/**").hasRole("CUSTOMER")

                        // Dashboard
                        .requestMatchers("/gui/dashboard").authenticated()

                        // any other request
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/gui/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler((request, response, authentication) -> {
                            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                            if (userDetails.getUserType() == UserType.MERCHANT) {
                                response.sendRedirect("/gui/merchant/profile");
                            } else {
                                response.sendRedirect("/gui/customer/profile");
                            }
                        })
                        .permitAll())
                .logout(logout -> logout.logoutUrl("/gui/logout")
                        .logoutUrl("/gui/logout")
                        .logoutSuccessUrl("/gui/login?logout")
                        .invalidateHttpSession(true)      // <--- clears session
                        .deleteCookies("JSESSIONID")      // <--- deletes session cookie
                        .permitAll()
                );
        return http.build();
    }
    @Bean
    public FilterRegistrationBean<ForwardedHeaderFilter> forwardedHeaderFilter() {
        ForwardedHeaderFilter filter = new ForwardedHeaderFilter();
        FilterRegistrationBean<ForwardedHeaderFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ASYNC, DispatcherType.ERROR);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }
}

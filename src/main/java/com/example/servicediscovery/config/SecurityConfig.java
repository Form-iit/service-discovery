package com.example.servicediscovery.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Value("${app.security.user.name}")
    private String username;
    @Value("${app.security.user.password}")
    private String password;

    @Bean
    public SecurityFilterChain filterChainConfiguration(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/actuator/**").hasRole("ADMIN")    // Require authentication for actuator endpoints
                        .requestMatchers("/eureka/**").hasRole("ADMIN")  // Allow Eureka dashboard access
                        .anyRequest().authenticated()  // Allow all other requests
                )
                .httpBasic(Customizer.withDefaults())  // Enable Basic Authentication (optional)
                .csrf(AbstractHttpConfigurer::disable)  // Disable CSRF for simplicity; adjust as needed
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder){

        UserDetails admin = User.builder()
                .username(username)
                .password(passwordEncoder().encode(password))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(admin);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // Password encoder
    }
}

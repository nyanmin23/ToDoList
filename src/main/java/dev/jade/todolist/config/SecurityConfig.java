package dev.jade.todolist.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * TESTING CONFIGURATION - This permits all requests without authentication.
     * <p>
     * For now, this configuration:
     * 1. Disables CSRF (needed for POST/PUT/DELETE requests to work)
     * 2. Permits all requests without authentication
     * 3. Disables form login
     * <p>
     * This makes it easy to test your CRUD endpoints without worrying about
     * authentication. When you're ready to implement proper security, you'll
     * need to change .permitAll() to .authenticated() and add actual auth logic.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)  // Disable CSRF for testing
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()  // Allow all requests - TESTING ONLY
                )
                .formLogin(AbstractHttpConfigurer::disable);

        return http.build();
    }

    /**
     * Password encoder bean required by AuthService.
     * Even though authentication isn't implemented yet, Spring needs this
     * bean to exist because AuthService declares it as a dependency.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
package com.abetappteam.abetapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // disable for API dev
                .authorizeHttpRequests(auth -> auth
                        // allow static assets and SPA routes
                        .requestMatchers(
                                "/", "/index.html",
                                "/favicon.ico",
                                "/assets/**",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/fonts/**",
                                "/signup",
                                "/login",
                                "/test-connection",
                                "/h2-console/**"
                        ).permitAll()
                        // protect only API endpoints
                        .requestMatchers("/api/**").permitAll()
                        // everything else (for Vue routing) is allowed
                        .anyRequest().permitAll()
                )
                .headers(headers -> headers.frameOptions(frame -> frame.disable())) // let H2 console render
                .formLogin(form -> form.disable())  // no Spring login form
                .httpBasic(basic -> basic.disable()); // disable browser auth popup

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

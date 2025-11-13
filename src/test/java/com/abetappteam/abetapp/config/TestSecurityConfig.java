package com.abetappteam.abetapp.config;

import com.abetappteam.abetapp.security.JwtUtil;
import com.abetappteam.abetapp.service.ProgramService;
import com.abetappteam.abetapp.service.UsersService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import static org.mockito.Mockito.mock;

/**
 * Test configuration to disable security for unit tests.
 * This allows @WebMvcTest tests to run without authentication.
 */
@TestConfiguration
@EnableWebSecurity
public class TestSecurityConfig {

    @Bean
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );
        return http.build();
    }

    @Bean
    @Primary
    public JwtUtil jwtUtil() {
        return mock(JwtUtil.class);
    }

    @Bean
    @Primary
    public UsersService usersService() {
        return mock(UsersService.class);
    }

    @Bean
    @Primary
    public org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder passwordEncoder() {
        return mock(org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.class);
    }

    @Bean
    @Primary
    public ProgramService programService() {
        return mock(ProgramService.class);
    }
}
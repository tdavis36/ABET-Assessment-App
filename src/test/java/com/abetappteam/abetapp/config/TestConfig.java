package com.abetappteam.abetapp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;

/**
 * Test configuration for Spring Boot tests.
 * Provides common beans and configuration needed across tests.
 */
@TestConfiguration
@ComponentScan(basePackages = "com.abetappteam.abetapp.util")
public class TestConfig {

    /**
     * ObjectMapper configured for testing with Java 8 time support
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

}
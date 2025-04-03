package com.ABETAppTeam;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Connection;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MariaDBContainer;

import com.ABETAppTeam.util.DataSourceFactory;

public class MariaDBContainerTest {

    @SuppressWarnings("resource")
    @Test
    public void testDatabaseConnection() throws Exception {
        try (MariaDBContainer<?> mariaDBContainer = new MariaDBContainer<>("mariadb:latest")
                .withDatabaseName("abetapp_test")
                .withUsername("test")
                .withPassword("test")) {

            mariaDBContainer.start();

            // Retrieve connection properties from the container
            String jdbcUrl = mariaDBContainer.getJdbcUrl();
            String username = mariaDBContainer.getUsername();
            String password = mariaDBContainer.getPassword();

            // Option 1: Set system properties for your DataSourceFactory to pick up
            System.setProperty("DB_URL", jdbcUrl);
            System.setProperty("DB_USERNAME", username);
            System.setProperty("DB_PASSWORD", password);

            // Option 2: Alternatively, inject these properties directly in your test-specific DataSourceFactory

            // Now test that the DataSource can obtain a connection
            try (Connection conn = DataSourceFactory.getDataSource().getConnection()) {
                assertNotNull(conn, "Connection should not be null");
            }
        }
    }
}

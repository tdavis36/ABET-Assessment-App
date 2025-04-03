package com.ABETAppTeam.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class DataSourceFactory {

    private static final HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();

        // Read from system properties – these can be set externally in your Maven profiles
        String jdbcUrl = System.getProperty("DB_URL", "jdbc:mariadb://localhost:3306/abetapp");
        String username = System.getProperty("DB_USERNAME", "root");
        String password = System.getProperty("DB_PASSWORD", "");

        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("org.mariadb.jdbc.Driver");

        // HikariCP settings
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        dataSource = new HikariDataSource(config);
    }

    public static DataSource getDataSource() {
        return dataSource;
    }
}

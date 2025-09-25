package com.techne.ChronoFlow.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    @Override
    public void run(String... args) throws Exception {
        String databaseName = "chrono_flow";
        try (Connection connection = dataSource.getConnection()) {
            String url = connection.getMetaData().getURL();
            databaseName = extractDatabaseNameFromUrl(url);
        } catch (SQLException e) {
            System.err.println("Failed to get database connection metadata: " + e.getMessage());
        }

        try {
            String sql = "CREATE DATABASE IF NOT EXISTS " + databaseName;
            jdbcTemplate.execute(sql);
            System.out.println("Database '" + databaseName + "' created or already exists.");
        } catch (Exception e) {
            System.err.println("Failed to create database '" + databaseName + "': " + e.getMessage());
        }
    }

    private String extractDatabaseNameFromUrl(String url) {
        try {
            int lastSlash = url.lastIndexOf("/");
            int questionMark = url.indexOf("?");
            if (questionMark == -1) {
                return url.substring(lastSlash + 1);
            }
            return url.substring(lastSlash + 1, questionMark);
        } catch (Exception e) {
            System.err.println("Could not extract database name from URL: " + url);
            return "chrono_flow"; // fallback
        }
    }
}

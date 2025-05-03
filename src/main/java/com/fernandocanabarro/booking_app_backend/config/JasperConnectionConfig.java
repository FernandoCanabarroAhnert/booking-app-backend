package com.fernandocanabarro.booking_app_backend.config;

import java.sql.Connection;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JasperConnectionConfig {

    @Bean
    public Connection connection(DataSource dataSource) {
        try {
            return dataSource.getConnection();
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to create Jasper connection", e);
        }
    }

}

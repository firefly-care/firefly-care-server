package org.farm.fireflyserver.common.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties defaultDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "dataSource")
    @Primary
    @Qualifier("defaultDataSource")
    @ConfigurationProperties("spring.datasource.hikari")
    public DataSource defaultDataSource() {
        return defaultDataSourceProperties().initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.led-data")
    public DataSourceProperties ledDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "ledDataSource")
    @ConfigurationProperties("spring.datasource.led-data.hikari")
    public DataSource ledDataSource(
            @Qualifier("ledDataSourceProperties") DataSourceProperties ledDataSourceProperties
    ) {
        return ledDataSourceProperties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }
}

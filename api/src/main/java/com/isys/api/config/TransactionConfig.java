package com.isys.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

// ...

@Configuration
@EnableTransactionManagement
public class TransactionConfig implements TransactionManagementConfigurer {

    @Autowired
    private DataSource firstDataSource;

    @Autowired
    private DataSource secondDataSource;

    @Bean
    public PlatformTransactionManager txManagerFirst() {
        return new DataSourceTransactionManager(firstDataSource);
    }

    @Bean
    public PlatformTransactionManager txManagerSecond() {
        return new DataSourceTransactionManager(secondDataSource);
    }

    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return txManagerFirst(); // 기본 트랜잭션 매니저 지정
    }
}


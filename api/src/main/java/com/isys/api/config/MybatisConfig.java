package com.isys.api.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

// ...

@Configuration
@MapperScan( basePackages = "com.isys.api.common.login.mapper", sqlSessionFactoryRef = "sqlSessionFactoryFirst")
public class MybatisConfig {

    @Bean
    @Primary
    public SqlSessionFactory sqlSessionFactoryFirst(DataSource firstDataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(firstDataSource);
        sessionFactory.setMapperLocations(
                new PathMatchingResourcePatternResolver().getResources("classpath:mybatis/mapper/LoginMapper.xml")
        );
        return sessionFactory.getObject();
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplateFirst(SqlSessionFactory sqlSessionFactoryFirst) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactoryFirst);
    }

}


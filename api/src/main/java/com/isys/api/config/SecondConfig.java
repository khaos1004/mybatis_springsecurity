package com.isys.api.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

@Configuration
@MapperScan( basePackages = "com.isys.api.business.alarm.mapper", sqlSessionFactoryRef = "sqlSessionFactorySecond")
public class SecondConfig {
    @Bean
    public SqlSessionFactory sqlSessionFactorySecond(DataSource secondDataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(secondDataSource);
        sessionFactory.setMapperLocations(
                new PathMatchingResourcePatternResolver().getResources("classpath:mybatis/mapper/AlarmMapper.xml")
        );
        return sessionFactory.getObject();
    }


    @Bean
    public SqlSessionTemplate sqlSessionTemplateSecond(SqlSessionFactory sqlSessionFactorySecond) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactorySecond);
    }
}

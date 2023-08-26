package com.qortex.inventory.common.config;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CriteriaBuilderConfigs {
 private final EntityManagerFactory entityManagerFactory;

 @Autowired
    public CriteriaBuilderConfigs(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Bean
    public CriteriaBuilder criteriaBuilder() {
        return entityManagerFactory.getCriteriaBuilder();
    }
}

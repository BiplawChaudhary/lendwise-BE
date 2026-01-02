package com.lendwise.iam.config.connection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lendwise.iam.utils.common.ApplicationPropertyHotLoader;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/*
    @created 1/1/2026 8:18 PM
    @project lendwise
    @author biplaw.chaudhary
*/
@Configuration
@EnableTransactionManagement
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "lendwise.postgres", havingValue = "true")
public class DBConfig {

    private final ConnectionPropertyReader secretsService;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Value("${jarFusion.entity.base.package}")
    private String entityBasePackage;


    @Bean
    @Primary
    public DataSource dataSource() {
        Map<String, Object> params = Objects.requireNonNull(getParams());
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(params.get("url").toString());
        dataSource.setUsername(params.get("username").toString());
        dataSource.setPassword(params.get("password").toString());
        dataSource.setDriverClassName("org.postgresql.Driver");

        dataSource.setPoolName("MyHikariPool");
        dataSource.setMinimumIdle(5);
        dataSource.setMaximumPoolSize(10);
        dataSource.setIdleTimeout(120000);    // 2 minutes
        dataSource.setMaxLifetime(300000);    // 5 minutes
        dataSource.setConnectionTimeout(20000); // 20 seconds
        dataSource.setValidationTimeout(5000); // 5 seconds

        // Connection testing settings
        dataSource.setConnectionTestQuery("SELECT 1");
        dataSource.setKeepaliveTime(60000);   // 1 minute

        return dataSource;
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);

        if (entityBasePackage != null && !entityBasePackage.isBlank()) {
            em.setPackagesToScan(entityBasePackage);
        } else {
            log.info("No 'app.entity.base-package' configured. Skipping entity scanning.");
            log.info("If you are using @Entity annotations, please provide 'app.entity.base-package' in your application properties.");
        }

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", "none");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.setProperty("hibernate.show_sql", "true");
        em.setJpaProperties(properties);

        return em;
    }


    @Bean
    @Primary
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }

    private Map<String, Object> getParams() {
        log.info("*********************Postgres Connection Params Fetching*********************");
        String rbdmsSecretString = secretsService.getValue("psqldb-sql");
        log.info("*********************Postgres Connection Params Fetching Complete*********************");
        String dbName = ApplicationPropertyHotLoader.getMessageKey("database.name");
        rbdmsSecretString = rbdmsSecretString.replace("{DATABASE_NAME}", dbName);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> rdbmsConnectionParamMap = new HashMap<>();
        try {
            rdbmsConnectionParamMap = mapper.readValue(rbdmsSecretString, Map.class);
            log.info("**********************Postgres Connection Params Mapped*********************");
        } catch (JsonProcessingException e) {
            log.info("*********************Postgres Connection Params Mapping Error*********************");
            return null;
        }
        return (Map<String, Object>) rdbmsConnectionParamMap.get(activeProfile);
    }
}
package gutsandgun.kite_log.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@PropertySource({ "classpath:application.yml" })
@EnableJpaRepositories(
        basePackages = "gutsandgun.kite_log.repository.read", // Master Repository 경로
        entityManagerFactoryRef = "ReadEntityManager",
        transactionManagerRef = "ReadTransactionManager"
)
public class ReadDBConfig {
    @Autowired
    private Environment env;

    @Bean
    public LocalContainerEntityManagerFactoryBean ReadEntityManager() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(ReadDataSource());

        //Entity 패키지 경로
        em.setPackagesToScan("gutsandgun.kite_log.entity.read");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        //Hibernate 설정
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto",env.getProperty("spring.jpa.hibernate.ddl-auto"));
        properties.put("hibernate.dialect",env.getProperty("spring.jpa.properties.hibernate.dialect"));
        properties.put("hibernate.dialect.storage_engine",env.getProperty("spring.jpa.properties.hibernate.storage_engine"));
        properties.put("hibernate.format_sql",env.getProperty("spring.jpa.properties.hibernate.format_sql"));
        properties.put("hibernate.show-sql",env.getProperty("spring.jpa.properties.hibernate.show-sql"));
        properties.put("hibernate.generate-ddl",env.getProperty("spring.jpa.properties.hibernate.generate-ddl"));
        em.setJpaPropertyMap(properties);
        return em;
    }

    @Bean
    @ConfigurationProperties(prefix="spring.readdb")
    public DataSource ReadDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public PlatformTransactionManager ReadTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(ReadEntityManager().getObject());
        return transactionManager;
    }
}

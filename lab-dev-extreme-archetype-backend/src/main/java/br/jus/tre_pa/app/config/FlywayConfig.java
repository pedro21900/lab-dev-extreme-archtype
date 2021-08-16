package br.jus.tre_pa.app.config;


import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class FlywayConfig {
	
	@Bean
	FlywayMigrationInitializer flywayInitializer(Flyway flyway) {
		return new FlywayMigrationInitializer(flyway, (f) -> {
			f.setBaselineOnMigrate(true);
			f.setBaselineVersionAsString("0");
		});
	}

	@Bean
	@DependsOn("entityManagerFactory")
	FlywayMigrationInitializer delayedFlywayInitializer(Flyway flyway) {
		log.info("Inicializando flyway");
		return new FlywayMigrationInitializer(flyway, null);
	}

    @Bean
    public Flyway flyway(DataSource dataSource) {
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        return flyway;
    }

}

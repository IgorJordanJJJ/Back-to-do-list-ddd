package jordan.pro.todo.smile.bootstrap.infrastructure.persistence.config

import org.flywaydb.core.Flyway
import org.springframework.boot.autoconfigure.flyway.FlywayProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import javax.sql.DataSource

/**
 * Конфигурация Flyway для миграций базы данных
 */
@Configuration
@EnableConfigurationProperties(FlywayProperties::class)
open class FlywayConfig {

    /**
     * Создает и настраивает экземпляр Flyway
     */
    @Bean(initMethod = "migrate")
    @Profile("!test")
    open // Не выполнять миграции в тестовом окружении
    fun flyway(dataSource: DataSource, flywayProperties: FlywayProperties): Flyway {
        return Flyway.configure()
            .dataSource(dataSource)
            .locations(*flywayProperties.locations.toTypedArray())
            .baselineOnMigrate(flywayProperties.isBaselineOnMigrate)
            .validateOnMigrate(true)
            .load()
    }
}
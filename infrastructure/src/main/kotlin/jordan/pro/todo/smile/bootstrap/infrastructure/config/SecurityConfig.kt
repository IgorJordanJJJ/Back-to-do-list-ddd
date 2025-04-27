package jordan.pro.todo.smile.bootstrap.infrastructure.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * Конфигурация компонентов безопасности
 */
@Configuration
open class SecurityConfig {

    /**
     * Создает кодировщик паролей BCrypt
     */
    @Bean
    open fun passwordEncoder(): PasswordEncoder {
        // Уровень сложности 12 - хороший баланс между безопасностью и производительностью
        return BCryptPasswordEncoder(12)
    }
}
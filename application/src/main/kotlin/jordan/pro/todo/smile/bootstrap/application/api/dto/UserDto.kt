package jordan.pro.todo.smile.bootstrap.application.api.dto

import java.time.LocalDateTime

/**
 * DTO для пользователя
 */
data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    // passwordHash не включаем в DTO для безопасности
    val hasActiveTokens: Boolean = false
)
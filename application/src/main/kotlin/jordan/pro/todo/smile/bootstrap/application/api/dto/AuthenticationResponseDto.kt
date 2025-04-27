package jordan.pro.todo.smile.bootstrap.application.api.dto

import java.time.LocalDateTime

/**
 * DTO для ответа на аутентификацию
 */
data class AuthenticationResponseDto(
    val userId: String,
    val token: String,
    val expiresAt: LocalDateTime,
    val name: String,
    val email: String
)
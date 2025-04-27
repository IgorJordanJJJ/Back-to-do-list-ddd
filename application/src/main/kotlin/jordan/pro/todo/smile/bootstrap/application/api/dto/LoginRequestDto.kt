package jordan.pro.todo.smile.bootstrap.application.api.dto

/**
 * DTO для запроса на вход
 */
data class LoginRequestDto(
    val email: String,
    val password: String
)
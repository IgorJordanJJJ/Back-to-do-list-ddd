package jordan.pro.todo.smile.bootstrap.application.api.dto

/**
 * DTO для запроса на регистрацию
 */
data class RegisterRequestDto(
    val name: String,
    val email: String,
    val password: String
)
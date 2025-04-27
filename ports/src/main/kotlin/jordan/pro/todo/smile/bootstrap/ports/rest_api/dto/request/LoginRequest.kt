package jordan.pro.todo.smile.bootstrap.ports.rest_api.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

/**
 * REST API запрос: вход пользователя
 */
data class LoginRequest(
    @field:NotBlank(message = "Email не может быть пустым")
    @field:Email(message = "Некорректный формат email")
    val email: String,

    @field:NotBlank(message = "Пароль не может быть пустым")
    val password: String
)
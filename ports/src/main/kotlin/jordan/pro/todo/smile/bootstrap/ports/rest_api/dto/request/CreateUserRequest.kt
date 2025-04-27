package jordan.pro.todo.smile.bootstrap.ports.rest_api.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/**
 * REST API запрос: создание пользователя
 */
data class CreateUserRequest(
    @field:NotBlank(message = "Имя не может быть пустым")
    @field:Size(min = 3, max = 255, message = "Имя должно содержать от 3 до 255 символов")
    val name: String,

    @field:NotBlank(message = "Email не может быть пустым")
    @field:Email(message = "Некорректный формат email")
    val email: String
)
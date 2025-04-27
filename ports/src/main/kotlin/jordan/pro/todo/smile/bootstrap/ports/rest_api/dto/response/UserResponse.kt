package jordan.pro.todo.smile.bootstrap.ports.rest_api.dto.response

/**
 * REST API ответ: общая информация о пользователе
 */
data class UserResponse(
    val id: String,
    val name: String? = null,
    val email: String? = null
)
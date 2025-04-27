package jordan.pro.todo.smile.bootstrap.ports.rest_api.dto.response

/**
 * REST API ответ: JWT токен
 */
data class TokenResponse(
    val token: String,
    val userId: String
)
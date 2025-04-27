package jordan.pro.todo.smile.bootstrap.ports.rest_api.dto.response

import java.time.LocalDateTime

/**
 * REST API ответ: ошибка
 */
data class ErrorResponse(
    val status: Int,
    val message: String,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val path: String? = null,
    val details: Map<String, Any>? = null
)